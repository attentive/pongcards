(ns pongcards.keypresses
  (:require [cljs.core.async :refer [put! <! chan timeout]]
            [goog.events :as events])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn listen-keypresses
  "Create a channel which emits specific keypresses as defined keywords."
  [el code-map]
  (let [out (chan)]
    (events/listen el "keypress"
                   (fn [e] (let [code (.-charCode e)
                                 keypress (get code-map code)]
                             ;(println "keypress: " code)
                             (if keypress (put! out keypress)))))
    out))

