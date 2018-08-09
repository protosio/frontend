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



;; -- Data subs for init step1 --------------------------------

(rf/reg-sub
  :init-form-step1
  (fn init-form-step1-sub
    [db _]
      (get-in db [:init-wizard :step1 :form])))


(rf/reg-sub
  :alert-init1
  (fn alert-init1-sub
    [db _]
      (-> db
          :init-wizard
          :step1
          :alert)))

;; -- Data subs for init step2 --------------------------------

(rf/reg-sub
  :init-form-step2
  (fn init-form-step2-sub
    [db _]
      (get-in db [:init-wizard :step2 :form])))

(rf/reg-sub
  :alert-init2
  (fn alert-init2-sub
    [db _]
      (-> db
          :init-wizard
          :step2
          :alert)))

(rf/reg-sub
  :dns-providers
  (fn dns-providers-sub
    [db _]
      (vec (map (fn [itm] {:id (get itm 0)
                           :name (get-in itm [1 :name])})
                (-> db
                    :init-wizard
                    :step2
                    :dns-provider-list)))))

(rf/reg-sub
  :selected-dns-provider
  (fn selected-dns-provider-sub
    [db _]
      (-> db
          :init-wizard
          :step2
          :selected-dns-provider)))

(rf/reg-sub
  :dns-provider-params
  (fn dns-provider-params-sub
    [db _]
      (-> db
          :init-wizard
          :step2
          :installer-run-params)))


)