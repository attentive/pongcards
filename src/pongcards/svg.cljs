(ns pongcards.svg
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom]
            [sablono.core :refer-macros [html]]
            [cljs.core.async :refer [<!]]
            [pongcards.clock :refer [clock]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]
                   [devcards.core :as dc :refer [defcard deftest]]))

(defn ball [cursor owner]
  (om/component
    (let [[x y] @cursor]
      (html [:circle {:cx x 
                      :cy y 
                      :r 10 
                      :stroke "black" 
                      :stroke-width "1" 
                      :fill "white"}]))))

(defn paddle [cursor owner]
  (om/component    
    (let [[[tlx tly] [brx bry]] @cursor] 
      (html [:rect {:x tlx 
                    :y tly 
                    :width (+ tlx (- brx tlx)) 
                    :height (+ tly (- bry tly))
                    :style {:fill "yellow"}}]))))

(defn pong [cursor owner]
  (om/component
    (html [:div 
           [:svg
            {:style {:background-color "#33bb77"}
             :width "100%" :height "500px"}
            (om/build ball (get cursor :ball))
            (om/build paddle (get cursor :1up))]])))


