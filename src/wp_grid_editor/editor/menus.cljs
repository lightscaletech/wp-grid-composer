(ns wp-grid-editor.editor.menus
  (:require-macros
   [javelin.core :as j]
   [hoplon.core :as h])
  (:require
   [javelin.core :as j]
   [hoplon.core :as h]))

(h/defelem master []
  (h/div :class "lsgc_main_menu"))


(h/defelem modual []
  (h/div :class "lsgc_modual_menu"))
