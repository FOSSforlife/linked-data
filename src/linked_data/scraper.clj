(ns linked-data.scraper
  (:require [hickory.core :as hickory]
            [hickory.select :as s]))

;; Web Scraping & Link Intelligence
;; As suggested: "Hickory: HTML -> Hiccup -> EDN"

(defn parse-html [html-content]
  (-> html-content
      hickory/parse
      hickory/as-hiccup))

(defn extract-links [html-content]
  (let [tree (-> html-content hickory/parse hickory/as-hickory)]
    (->> (s/select (s/tag :a) tree)
         (map (fn [node]
                {:href (get-in node [:attrs :href])
                 :text (first (:content node))}))
         (filter :href))))

(defn extract-headers [html-content]
  (let [tree (-> html-content hickory/parse hickory/as-hiccup)]
    ;; This is a simplified extraction, in a real app we might use zippers or selectors
    ;; But for now, let's just show we can parse it.
    tree))

(comment
  ;; REPL Experiments
  (def sample-html "<html><body><h1>Hello</h1><a href='https://clojure.org'>Clojure</a></body></html>")
  (parse-html sample-html)
  (extract-links sample-html)
  )