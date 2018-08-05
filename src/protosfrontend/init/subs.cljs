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
      (get-in db [:init-wizard :step])))

(rf/reg-sub
  :init-form-step1
  (fn init-form-step1-sub
    [db _]
      (get-in db [:init-wizard :step1])))

(rf/reg-sub
  :init-form-step2
  (fn init-form-step2-sub
    [db _]
      (get-in db [:init-wizard :step2])))

;; -- Alert subs for init steps --------------------------------

(rf/reg-sub
  :alert-init1
  (fn alert-init1-sub
    [db _]
      (-> db
          :alert
          :init-step1)))

(rf/reg-sub
  :alert-init2
  (fn alert-init2-sub
    [db _]
      (-> db
          :alert
          :init-step2)))

)