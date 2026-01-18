(ns linked-data.rdf-export
  (:require [datascript.core :as d]))

;; RDF / Semantic Web (Without Committing Early)
;; "Paradigm: EDN-Native, RDF-Capable"
;; "Add translators at the boundary"

(defn note-to-triples [db note-id]
  (let [entity (d/entity db [:note/id note-id])
        subject (str "http://example.org/note/" note-id)]
    (concat
     ;; Content
     [[subject "http://example.org/schema/content" (:note/content entity)]]
     ;; Tags
     (for [tag (:note/tags entity)]
       [subject "http://example.org/schema/tag" tag])
     ;; Links
     (for [link (:note/links entity)]
       [subject "http://example.org/schema/linksTo" (str "http://example.org/note/" (:note/id link))]))))

(defn export-db-as-ntriples [db]
  (let [ids (d/q '[:find [?id ...] :where [?e :note/id ?id]] db)]
    (mapcat #(note-to-triples db %) ids)))

(defn triples-to-string [triples]
  (apply str
         (for [[s p o] triples]
           (format "<%s> <%s> \"%s\" .\n" s p o))))

(comment
  ;; Assuming we have a db connection from core
  ;; (require '[linked-data.core :as core])
  ;; (triples-to-string (export-db-as-ntriples @core/db-conn))
  )