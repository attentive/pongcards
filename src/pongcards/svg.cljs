(ns pongcards.svg
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom]
            [sablono.core :refer-macros [html]]
            [cljs.core.async :refer [<!]]
            [pongcards.clock :refer [clock]]
            [pongcards.keypresses :refer [listen-keypresses sub-keypresses]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]
                   [devcards.core :as dc :refer [defcard deftest]]))

(def PADDLE-WIDTH 10)
(def PADDLE-LENGTH 25)
(def RADIUS 10)

(defn ball [cursor owner]
  (om/component
    (let [[x y] (:position cursor)]
      (html [:circle {:cx x 
                      :cy y 
                      :r RADIUS 
                      :stroke "black" 
                      :stroke-width "1" 
                      :fill "white"}]))))

(defn paddle [cursor owner]
  (om/component    
    (let [[x y] (:position cursor)
          tlx (- x PADDLE-WIDTH)
          tly (- y PADDLE-LENGTH)
          w (* 2 PADDLE-WIDTH)
          h (* 2 PADDLE-LENGTH)] 
      (html [:rect {:x tlx 
                    :y tly 
                    :width w 
                    :height h
                    :style {:fill "yellow"
                            :stroke "black" 
                            :stroke-width "1"}}]))))

(defn pong [cursor owner]
  (om/component
    (html [:div {:style {:height "100%"}}
           [:svg
            {:style {:background-color "#33bb77"
                     :border-width "3px" 
                     :border-color "white"}
             :width "100%" :height "600px"}
            (om/build ball (get cursor :ball))
            (om/build paddle (get cursor :1up))
            (om/build paddle (get cursor :2up))]])))

(defn animated-pong 
  [cursor owner {:keys [clock-interval]}]
  (reify
    om/IInitState
    (init-state [_]
      {:clock (clock (or clock-interval 10))})
    om/IWillMount
    (will-mount [_]
      (let [[clock] (om/get-state owner :clock)]
        (go-loop []
                 (let [_ (<! clock)
                       ball (:ball @cursor)] 
                   (om/update! cursor [:ball 0] (mod (+ 2 (ball 0)) 1000))
                   (om/update! cursor [:ball 1] (mod (+ 2 (ball 1)) 800))
                   (recur)))))
    om/IRender
    (render [_]
      (om/build pong cursor))))

(defonce pong-keypresses 
  (listen-keypresses (.-body js/document)
                     {119 :1up-up
                      115 :1up-down
                      111 :2up-up
                      108 :2up-down
                      13 :enter}))

(defn sub-pong-keypresses []
  (sub-keypresses pong-keypresses))                  

(defn interactive-pong
  [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
      (let [keypresses (sub-pong-keypresses)]
        (go-loop []
                 (let [keypress (<! keypresses)]
                   (om/set-state! owner :text (str keypress)))
                 (recur)))
      {:text "waiting for first keypress ..."})
    om/IRenderState
    (render-state [_ {:keys [text]}]
      (html [:div [:h4 text]]))))


(defn moving-pong
  [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
      (let [keypresses (sub-pong-keypresses)]
        (go-loop []
                 (let [keypress (<! keypresses)]
                   (om/set-state! owner :text (str keypress)))
                 (recur)))
      {:text "waiting ..."})
    om/IRenderState
    (render-state [_ {:keys [text]}]
      (html [:div [:h4 text]]))))


