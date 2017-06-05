(ns wp-grid-editor.editor
  (:require-macros
   [hoplon.core :as h]
   [javelin.core :as j])
  (:require
   [hoplon.core :as h]
   [javelin.core :as j]))

(h/defelem menu-bar-main []
  (h/div
   :class "lsgc_main_bar"
   ))

(h/defelem editor []
  (h/div
   :class "lsgc_editor_wrap"
   ))
