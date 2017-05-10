(ns pongcards.core
  (:require
    [clojure.string :refer [join]]
    [om.core :as om]
    [sablono.core :as sab :include-macros true]
    [pongcards.canvas :refer [pong-field pong-animations]]
    [pongcards.svg :refer [pong animated-pong interactive-pong moving-pong]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard svg-card
  (dc/om-root animated-pong)
  {:ball 
   {:position [50 50] :speed [1 1] :acceleration [0 0]} 
   :1up 
   {:position [25 25] :speed [0 0] :acceleration [0 0]}
   :2up 
   {:position [600 75] :speed [0 0] :acceleration [0 0]}}
  {:padding false}) 

(defcard canvas-card
  (dc/om-root pong-field)
  (atom pong-animations)
  {:padding false})

(defcard keypress-card
  (dc/om-root interactive-pong))

(defcard moving-paddle-card
  (dc/om-root moving-pong))

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

