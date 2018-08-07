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
                     [:get-dns-providers]
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
                     [:get-dns-providers]
                     [:noop])]
          {:dispatch event
           :db (assoc-in db [:init-wizard :step] final-step)})))

(rf/reg-event-fx
  :register-user-domain
  (fn register-user-domain-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (pe/createurl ["auth" "register"])
                            :response-options {:alert-message "User and domain registered successfully" :alert-key :init-step1 :db-key [:auth] :event :save-auth}
                            :post-data (get-in db [:form-data :init-wizard :step1])}]}))

(rf/reg-event-fx
  :get-dns-providers
  (fn get-dns-providers-handler
    [_ _]
    {:dispatch [:http-get {:url (str (pe/createurl ["e" "store" "search"]) "?provides=dns")
                           :response-options {:alert-message "DNS providers retrieved successfully" :db-key [:init-wizard :step2 :dns-provider-list] :alert-key :init-step2}}]}))

(rf/reg-event-fx
  :download-dns-provider
  (fn download-dns-provider-handler
    [{db :db} [_ installer-name]]
    (println (-> db
      :init-wizard
      :step2
      :dns-provider-list))
    {:dispatch [:http-post {:url (pe/createurl ["e" "store" "download"])
                            :response-options {:alert-message (str "DNS provider " installer-name " downloaded successfully") :alert-key :init-step2}
                            :response-format :raw
                            :post-data (let [id (get-in db [:form-data :init-wizard :step2 :selected-dns-provider])
                                             installers (get-in db [:init-wizard :step2 :dns-provider-list])
                                             name (:name (get installers id))
                                             version (last (sort (:versions (get installers id))))]
                                       {:version version :name installer-name})}]}))


)