(ns init.subs
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "blue")}

;; -- Init subs -----------------------------------------------

(rf/reg-sub
  :init-step
  (fn init-step-sub
    [db _]
      (:init-step db)))

)