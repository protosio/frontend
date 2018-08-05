(ns init.events
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [protosfrontend.events :as pe]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

(rf/reg-event-fx
  :increment-init-step
  (fn increment-init-step-handler
    [{db :db} _]
    (let  [init-step (get-in db [:init-wizard :step])
           new-init-step (+ init-step 1)
           final-step (if (= :init-step 4)
                           4
                           new-init-step)
           event (if (= final-step 2)
                    ;  [:get-dns-providers]
                     [:noop]
                     [:noop])]
          {:dispatch event
           :db (assoc-in db [:init-wizard :step] final-step)})))

(rf/reg-event-fx
  :decrement-init-step
  (fn decrement-init-step-handler
    [{db :db} _]
    (let  [init-step (get-in db [:init-wizard :step])
           new-init-step (- init-step 1)
           final-step (if (= :init-step 1)
                           1
                           new-init-step)
           event (if (= final-step 2)
                    ;  [:get-dns-providers]
                     [:noop]
                     [:noop])]
          {:dispatch event
           :db (assoc-in db [:init-wizard :step] final-step)})))

(rf/reg-event-fx
  :register-user-domain
  (fn register-user-domain-handler
    [{db :db} [_ _]]
    {:dispatch [:http-post {:url (pe/createurl ["auth" "register"])
                            :response-options {:alert-message "User and domain registered successfully" :alert-key :init-step1 :db-key :auth :event :save-auth}
                            :post-data (get-in db [:form-data :init-wizard :step1])}]
     :db db}))

(rf/reg-event-fx
  :get-dns-providers
  (fn get-dns-providers-handler
    [{db :db} [_ _]]
    {:dispatch [:http-get {:url (pe/createurl ["e" "store" "search"])
                           :response-options {:db-key [:init-wizard :dns-provider-list]}}]
     :db db}))

)