(ns linked-data.analysis
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

;; Statistical Exploration
;; "Easy ingestion of external datasets"

(defn read-csv [file-path]
  (with-open [reader (io/reader file-path)]
    (doall
     (csv/read-csv reader))))

(defn csv-to-maps [csv-data]
  (let [headers (map keyword (first csv-data))
        rows (rest csv-data)]
    (map (fn [row] (zipmap headers row)) rows)))

(comment
  ;; REPL Experiments
  ;; (def data (read-csv "resources/data.csv"))
  ;; (csv-to-maps data)
  )