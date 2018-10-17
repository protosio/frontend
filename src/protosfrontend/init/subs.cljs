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
    (apply hash-map (flatten (map (fn [itm] (let [kid (get itm 0)
                                                  provider (get itm 1)
                                                  version (last (sort (keys (:versions provider))))]
                                                  [kid  {:id (:id provider)
                                                         :name (:name provider)
                                                         :version version
                                                         :provider-params (get-in provider [:versions version :params])}]))
                              (-> db
                                  :init-wizard
                                  step
                                  :provider-list))))))

(rf/reg-sub
  :provider-params
  (fn provider-params-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
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
  :init-step-task
  (fn init-step-task-sub
    [db [_ step]]
    (let [step-task-id (get-in db [:init-wizard step :task])
          step-task (get-in db [:tasks step-task-id])]
      step-task)))

(rf/reg-sub
  :alert-init
  (fn alert-init-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
          :alert)))

(rf/reg-sub
  :init-step-done
  (fn init-step-done-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
          :done)))

(rf/reg-sub
  :init-step-inprogress
  (fn init-step-inprogress-sub
    [db [_ step]]
      (-> db
          :init-wizard
          step
          :inprogress)))

;; -- Step 4 subs -----------------------------------------------

(rf/reg-sub
  :init-resources
  (fn init-resources-sub
    [db _]
      (-> db
          :init-wizard
          :step4
          :resources)))

)