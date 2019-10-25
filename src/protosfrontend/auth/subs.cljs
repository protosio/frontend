(ns protosfrontend.auth.subs
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "blue")}

(rf/reg-sub
  :alert-login
  (fn alert-login-sub
    [db _]
      (-> db
          :login
          :alert)))

)