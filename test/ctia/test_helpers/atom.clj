(ns ctia.test-helpers.atom
  (:require [ctia.store :refer [stores]]
            [ctia.test-helpers.core :refer [with-properties]]))

(defn fixture-properties:atom-memory-store [f]
  ;; Set properties to enable the atom store
  (with-properties ["ctia.store.actor" "atom"
                    "ctia.store.atom.actor.mode" "memory"
                    "ctia.store.campaign" "atom"
                    "ctia.store.atom.campaign.mode" "memory"
                    "ctia.store.coa" "atom"
                    "ctia.store.atom.coa.mode" "memory"
                    "ctia.store.exploit-target" "atom"
                    "ctia.store.atom.exploit-target.mode" "memory"
                    "ctia.store.feedback" "atom"
                    "ctia.store.atom.feedback.mode" "memory"
                    "ctia.store.identity" "atom"
                    "ctia.store.atom.identity.mode" "memory"
                    "ctia.store.incident" "atom"
                    "ctia.store.atom.incident.mode" "memory"
                    "ctia.store.indicator" "atom"
                    "ctia.store.atom.indicator.mode" "memory"
                    "ctia.store.judgement" "atom"
                    "ctia.store.atom.judgement.mode" "memory"
                    "ctia.store.verdict" "atom"
                    "ctia.store.atom.verdict.mode" "memory"
                    "ctia.store.sighting" "atom"
                    "ctia.store.atom.sighting.mode" "memory"
                    "ctia.store.ttp" "atom"
                    "ctia.store.atom.ttp.mode" "memory"]
    (f)))

(defn fixture-properties:atom-durable-store [f]
  ;; Set properties to enable the atom store
  (with-properties ["ctia.store.actor" "atom"
                    "ctia.store.atom.actor.mode" "durable"
                    "ctia.store.atom.actor.path" "/tmp/durable-actor.edn"
                    "ctia.store.campaign" "atom"
                    "ctia.store.atom.campaign.mode" "durable"
                    "ctia.store.atom.campaign.path" "/tmp/durable-campaign.edn"
                    "ctia.store.coa" "atom"
                    "ctia.store.atom.coa.mode" "durable"
                    "ctia.store.atom.coa.path" "/tmp/durable-coa.edn"
                    "ctia.store.exploit-target" "atom"
                    "ctia.store.atom.exploit-target.mode" "durable"
                    "ctia.store.atom.exploit-target.path" "/tmp/durable-exploit.edn"
                    "ctia.store.feedback" "atom"
                    "ctia.store.atom.feedback.mode" "durable"
                    "ctia.store.atom.feedback.path" "/tmp/durable-feeedback.edn"
                    "ctia.store.identity" "atom"
                    "ctia.store.atom.identity.mode" "durable"
                    "ctia.store.atom.identity.path" "/tmp/durable-identity.edn"
                    "ctia.store.incident" "atom"
                    "ctia.store.atom.incident.mode" "durable"
                    "ctia.store.atom.incident.path" "/tmp/durable-incident.edn"
                    "ctia.store.indicator" "atom"
                    "ctia.store.atom.indicator.mode" "durable"
                    "ctia.store.atom.indicator.path" "/tmp/durable-indicator.edn"
                    "ctia.store.judgement" "atom"
                    "ctia.store.atom.judgement.mode" "durable"
                    "ctia.store.atom.judgement.path" "/tmp/durable-judgement.edn"
                    "ctia.store.verdict" "atom"
                    "ctia.store.atom.verdict.mode" "durable"
                    "ctia.store.atom.verdict.path" "/tmp/durable-verdict.edn"
                    "ctia.store.sighting" "atom"
                    "ctia.store.atom.sighting.mode" "durable"
                    "ctia.store.atom.sighting.path" "/tmp/durable-sighting.edn"
                    "ctia.store.ttp" "atom"
                    "ctia.store.atom.ttp.mode" "durable"
                    "ctia.store.atom.ttp.path" "/tmp/durable-ttp.edn"]
    (f)))

(defn reset-atom-stores! []
  (doseq [store-vec (vals @stores)
          store store-vec
          :let [state (:state store)]]
    (when (instance? clojure.lang.IAtom state)
      (reset! state {}))))

(defn fixture-reset-stores [f]
  (reset-atom-stores!)
  (f)
  (reset-atom-stores!))
