(ns init.events
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [protosfrontend.events :as pe]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

(rf/reg-event-db
  :increment-init-step
  (fn increment-init-step-handler
    [db [_ _]]
    (assoc db :init-step (if (= (:init-step db) 4)
                            4
                            (+ (:init-step db) 1)))))

(rf/reg-event-db
  :decrement-init-step
  (fn decrement-init-step-handler
    [db [_ _]]
    (assoc db :init-step (if (= (:init-step db) 1)
                            1
                            (- (:init-step db) 1)))))

(rf/reg-event-fx
  :register-user-domain
  (fn register-user-domain-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (pe/createurl ["register"])
                            :response-options {:message "User and domain registered successfully" :db-key :userreg}
                            :post-data (:form-data db)}]
     :db db}))

)