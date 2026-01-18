(ns linked-data.core
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.coercion.malli :as coercion-malli]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [datascript.core :as d]
            [markdown.core :as md]
            [hickory.core :as hickory]
            [malli.core :as m]))

;; 1. Data-First / Schema-Last Design
;; We'll start with a simple in-memory atom to hold our "notes" for now,
;; but we will quickly move to DataScript.
(defonce db-conn (d/create-conn {:note/id {:db/unique :db.unique/identity}
                                 :note/tags {:db/cardinality :db.cardinality/many}
                                 :note/links {:db/cardinality :db.cardinality/many :db/valueType :db.type/ref}}))

;; 2. Graph-Native Thinking
;; Helper to add a note to our graph
(defn add-note! [conn id content tags links]
  (d/transact! conn [{:note/id id
                      :note/content content
                      :note/tags tags
                      :note/links (mapv (fn [link-id] [:note/id link-id]) links)}]))

;; Seed some data
(defn seed-data! []
  (add-note! db-conn "note-1" "This is a note about Clojure." #{"clojure" "programming"} [])
  (add-note! db-conn "note-2" "This note links to the first one." #{"linking"} ["note-1"]))

;; 3. REPL-First, HTTP-Last
;; We define our query functions first, which can be tested in the REPL.
(defn get-all-notes [db]
  (d/q '[:find ?id ?content ?tags
         :where
         [?e :note/id ?id]
         [?e :note/content ?content]
         [?e :note/tags ?tags]]
       db))

(defn get-note-by-id [db id]
  (d/q '[:find ?content ?tags
         :in $ ?id
         :where
         [?e :note/id ?id]
         [?e :note/content ?content]
         [?e :note/tags ?tags]]
       db id))

;; HTTP Handlers
(defn list-notes-handler [_]
  {:status 200
   :body (let [results (get-all-notes @db-conn)]
           (mapv (fn [[id content tags]]
                   {:id id :content content :tags tags})
                 results))})

(defn get-note-handler [req]
  (let [id (get-in req [:path-params :id])
        result (get-note-by-id @db-conn id)]
    (if (seq result)
      (let [[content tags] (first result)]
        {:status 200
         :body {:id id :content content :tags tags}})
      {:status 404 :body {:error "Note not found"}})))

;; Markdown parsing example
(defn parse-markdown-handler [req]
  (let [markdown-text (slurp (:body req))
        html (md/md-to-html-string markdown-text)]
    {:status 200
     :body {:html html}}))

;; Routes
(def app
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/notes" {:get list-notes-handler}]
      ["/notes/:id" {:get get-note-handler}]
      ["/markdown" {:post parse-markdown-handler}]]
     ["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "Linked Data API"}}
             :handler (swagger/create-swagger-handler)}}]]
    {:data {:coercion coercion-malli/coercion
            :middleware [swagger/swagger-feature]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/api-docs"})
    (ring/create-default-handler))))

(defn start-server []
  (seed-data!)
  (jetty/run-jetty #'app {:port 3000 :join? false}))

(comment
  (start-server)
  ;; REPL experiments
  (get-all-notes @db-conn)
  )