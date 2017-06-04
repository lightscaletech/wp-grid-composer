(ns hoplon-widget-demo.core
  (:require-macros
   [javelin.core :refer [cell= dosync]]
   [hoplon.core :as h])
  (:require
   [hoplon.jquery]
   [hoplon.core :as h]
   [javelin.core :refer [cell]]))

(defn ^:export js-args [params body])

(h/defelem ^:export todo-list []
  (let [item-list (cell [])
        item-new (cell "")
        add! #(dosync
               (swap! item-list conj @item-new)
               (reset! item-new ""))]
    (h/div
     (h/input :type "text"
              :value item-new
              :change #(reset! item-new @%))
     (h/button :click add! "Add")
     (h/ol
      (h/loop-tpl
       :bindings [i item-list]
       (h/li i))))))
