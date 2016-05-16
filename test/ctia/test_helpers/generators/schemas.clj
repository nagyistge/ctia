(ns ctia.test-helpers.generators.schemas
  (:require [clojure.test.check.generators :as gen]
            [ctia.domain.id :as id]
            [ctia.properties :refer [properties]]
            [ctia.schemas
             [feedback :refer [Feedback]]
             [identity :refer [Identity]]
             [verdict :refer [Verdict]]]
            [ctia.test-helpers.generators.common :refer [generate-entity]]
            [ctia.test-helpers.generators.schemas
             [actor-generators :as ag]
             [campaign-generators :as cg]
             [coa-generators :as og]
             [exploit-target-generators :as eg]
             [incident-generators :as ig]
             [indicator-generators :as ng]
             [judgement-generators :as jg]
             [sighting-generators :as sg]
             [ttp-generators :as tg]]))

(def gen-new-indicator-with-new-sightings
  (gen/fmap
   (fn [[{id :id :as indicator} sightings]]
     (let [indicator-url-id
           (id/long-id
            (id/short-id->id :indicator
                             id
                             (get-in @properties [:ctia :http :show])))]
       [indicator
        (map (fn [sighting]
               (assoc sighting :indicators
                      [{:indicator_id indicator-url-id}]))
             sightings)]))
   (gen/tuple ng/gen-new-indicator-with-id
              (gen/vector sg/gen-new-sighting 1 10))))

(def kw->generator
  {:actor          ag/gen-actor
   :new-actor      ag/gen-new-actor
   :campaign       cg/gen-campaign
   :new-campaign   cg/gen-new-campaign
   :coa            og/gen-coa
   :new-coa        og/gen-new-coa
   :exploit-target eg/gen-exploit-target
   :new-exploit-target eg/gen-new-exploit-target
   :feedback       (generate-entity Feedback)
   :identity       (generate-entity Identity)
   :incident       ig/gen-incident
   :new-incident   ig/gen-new-incident
   :indicator      ng/gen-indicator
   :new-indicator  ng/gen-new-indicator
   :judgement      jg/gen-judgement
   :new-judgement  jg/gen-new-judgement
   :sighting       sg/gen-sighting
   :new-sighting   sg/gen-new-sighting
   :ttp            tg/gen-ttp
   :new-ttp        tg/gen-new-ttp
   :verdict        (generate-entity Verdict)})

(defn gen-entity [schema-kw]
  (get kw->generator schema-kw))

(def default-complexity 20)

(defn sample-by-kw
  ([schema-kw]
   (sample-by-kw default-complexity schema-kw))
  ([num schema-kw]
   "generate num records of a schema-kw"
   (gen/sample (get kw->generator schema-kw)
               num)))
