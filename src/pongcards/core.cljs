(ns pongcards.core
  (:require
    [clojure.string :refer [join]]
    [om.core :as om]
    [sablono.core :as sab :include-macros true]
    [pongcards.canvas :refer [pong-field pong-animations]]
    [pongcards.svg :refer [pong]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard svg-card
  (dc/om-root pong)
  {:ball [50 50] 
   :1up [[25 40] [30 70]]}
  {:padding false}) 

(defcard canvas-card
  (dc/om-root pong-field)
  (atom pong-animations)
  {:padding false})


(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

