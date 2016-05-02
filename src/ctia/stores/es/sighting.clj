(ns ctia.stores.es.sighting
  (:require
   [schema.core :as s]
   [ctia.stores.es.crud :as crud]
   [ctia.stores.es.query :as query]
   [ctia.schemas.sighting :refer [Sighting
                                  NewSighting
                                  StoredSighting
                                  realize-sighting]]
   [ctia.schemas.indicator :refer [Indicator]]
   [ctia.lib.es.document :refer [raw-search-docs
                                 search-docs]]))


(def handle-create-sighting (crud/handle-create :sighting StoredSighting))
(def handle-read-sighting (crud/handle-read :sighting StoredSighting))
(def handle-update-sighting (crud/handle-update :sighting StoredSighting))
(def handle-delete-sighting (crud/handle-delete :sighting StoredSighting))
(def handle-list-sightings (crud/handle-find :sighting StoredSighting))

(def ^{:private true} mapping "sighting")

(defn handle-list-sightings-by-indicators
  [state indicators]
  (let [indicator-ids (mapv :id indicators)]
    (handle-list-sightings state {:type "sighting"
                                  [:indicators :indicator_id]
                                  indicator-ids})))

(defn handle-list-sightings-by-observables
  [{:keys [conn index]}  observables]

  (raw-search-docs conn
                   index
                   mapping
                   (query/sightings-by-observables-query observables)
                   {}))
