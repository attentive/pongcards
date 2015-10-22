(ns pongcards.core
  (:require
    [clojure.string :refer [join]]
    [om.core :as om]
    [sablono.core :as sab :include-macros true]
    [pongcards.animator :refer [animator]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defn screen-pt [[x y] scale h] [(* scale x) (- h (* scale y))])

(defn color->css [{:keys [r g b a] :or {a 1.0}}]
  (str "rgba(" (join "," [r g b a]) ")"))

(defn draw-circle
  [context center radius line-width scale color]
  (let [h (.-height (.-canvas context))
        [center-x center-y] (screen-pt center scale h)
        radius (* scale radius)]
    (set! (. context -strokeStyle) (color->css color))
    (set! (. context -lineWidth) line-width)
    (.beginPath context)
    ;; x y radius startAngle endAngle counterClockwise?:
    (.arc context center-x center-y radius 0 (* 2 Math/PI) false)
    (.stroke context)))

(defn draw-rect
  [context corners scale color]
  (let [h (.. context -canvas -height)
        [[tlx tly] [brx bry]] (map #(screen-pt % scale h) corners)]
    (set! (.-fillStyle context) (color->css color))
    (.fillRect context tlx tly brx bry)))

(def FIELD-COLOR {:r 51 :g 187 :b 119})
(def BALL-COLOR {:r 255 :g 255 :b 255})

(defn pong-field-update [_ canvas {:keys [width height color]}]
  (let [context (.getContext canvas "2d")]
    (draw-rect context [[0 0] [width height]] 1.0 color)))

(defn pong-circle-update [elapsed-time canvas {:keys [x y radius color]}]
 ;(println elapsed-time) 
  (let [context (.getContext canvas "2d")]
    (draw-circle context [(+ x (* 100 (.sin js/Math elapsed-time))) 
                          (+ y (* 100 (.cos js/Math elapsed-time)))] radius 1 1.0 color)))

(defonce pong-animations [{:update pong-field-update
                           :width 800 :height 800 :color FIELD-COLOR}
                          {:update pong-circle-update
                           :x 200 :y 200
                           :radius 5 :color BALL-COLOR}])

(defn pong-field [cursor owner]
  (reify
    om/IRender
    (render [_]
      (sab/html [:div {:width 800 :height 800}
                 (om/build animator
                           cursor
                           {:state {:start-time (.now (.-performance js/window))}
                                    :opts {:clock-interval 100}})]))))

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

