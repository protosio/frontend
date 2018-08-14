(ns init.subs
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "blue")}

;; -- Init common subs -----------------------------------------------

(rf/reg-sub
  :init-step
  (fn init-step-sub
    [db _]
      (get-in db [:init-wizard :step])))

(rf/reg-sub
  :selected-provider
  (fn selected-provider-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
          :selected-provider)))

(rf/reg-sub
  :providers
  (fn providers-sub
    [db [_ step]]
      (vec (map (fn [itm] {:id (get itm 0)
                           :name (get-in itm [1 :name])})
                (-> db
                    :init-wizard
                    step
                    :provider-list)))))

(rf/reg-sub
  :provider-params
  (fn provider-params-sub
    [db [_ step]]
      (-> db
          :init-wizard
          :step
          :installer-run-params)))

(rf/reg-sub
  :init-form
  (fn init-form-sub
    [db [_ step]]
      (get-in db [:init-wizard step :form])))

(rf/reg-sub
  :init-installer-downloaded
  (fn init-installer-downloaded-sub
    [db [_ step]]
      (get-in db [:init-wizard step :downloaded])))

(rf/reg-sub
  :alert-init
  (fn alert-init-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
          :alert)))

)