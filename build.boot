(set-env!
 :project       'wp-grid-composer
 :version       "0.0.1"
 :dependencies '[[org.clojure/clojurescript "1.7.228"]
                 [adzerk/boot-cljs          "1.7.228-2"]
                 [hoplon                    "6.0.0-alpha17"]]
 :source-paths #{"src"}
 :resource-paths #{"php" "res"})

(require '[boot.util :as butl]
         '[adzerk.boot-cljs   :refer [cljs]]
         '[clojure.edn :as edn]
         '[clojure.java.shell :only [sh]])

(defn rsync []
  (try
    (let [f (slurp "rsync.edn")
          {:keys [host user source destination]} (edn/read-string f)]
      (butl/info "Rsync files to server\n")
      (sh "rsync" "-a" "-e" "ssh" "--delete"
          source (str user "@" host ":" destination))
      (butl/info "Rsync completed\n"))
    (catch Exception e
      (butl/warn "There was an issue loading rsync settings file "
                  "rsync would not of worked.\n"))))

(deftask server-sync
  "Sync build to server for development."
  []
  (with-post-wrap fs (rsync)))

(deftask dev
  "Build development."
  []
  (comp
   (watch)
   (speak)
   (cljs :compiler-options {:source-map true})
   (target :dir #{"target"})
   (server-sync)))

(deftask prod
  "Build prod"
  []
  (comp
   (watch)
   (speak)
   (cljs :optimizations :advanced)
   (target :dir #{"target"})))
