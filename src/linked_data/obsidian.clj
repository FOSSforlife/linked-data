(ns linked-data.obsidian
  (:require [clj-yaml.core :as yaml]
            [clojure.string :as str]))

;; Markdown & Text Intelligence + YAML Frontmatter
;; "Parse YAML headers cleanly"

(defn parse-frontmatter [markdown-content]
  (let [matcher (re-matcher #"(?s)^---\n(.*?)\n---\n(.*)$" markdown-content)]
    (if (re-find matcher)
      (let [[_ yaml-str content] (re-groups matcher)]
        {:metadata (yaml/parse-string yaml-str)
         :content (str/trim content)})
      {:metadata {}
       :content markdown-content})))

(comment
  (def sample-note
    "---\ntitle: My Note\ntags:\n  - clojure\n  - data\n---\n# This is the content\n\nSome text here.")

  (parse-frontmatter sample-note)
  ;; => {:metadata {:title "My Note", :tags ["clojure" "data"]}, :content "# This is the content\n\nSome text here."}
  )