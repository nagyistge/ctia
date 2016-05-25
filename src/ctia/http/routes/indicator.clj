(ns ctia.http.routes.indicator
  (:require [compojure.api.sweet :refer :all]
            [ctia
             [properties :refer [properties]]
             [store :refer :all]]
            [ctia.domain.id :as id]
            [ctia.flows.crud :as flows]
            [ctia.http.routes.common :refer [paginated-ok PagingParams]]
            [ctia.schemas
             [common :as c]
             [indicator :refer [NewIndicator realize-indicator StoredIndicator]]
             [sighting :refer [StoredSighting]]]
            [ring.util.http-response :refer :all]
            [schema-tools.core :as st]
            [schema.core :as s]))

(s/defschema IndicatorsByTitleQueryParams
  (st/merge
   PagingParams
   {(s/optional-key :sort_by) (s/enum :id :title)}))

(s/defschema IndicatorsListQueryParams
  (st/merge
   PagingParams
   {(s/optional-key :sort_by) (s/enum :id :title)}))

(s/defschema SightingsByIndicatorQueryParams
  (st/merge
   PagingParams
   {(s/optional-key :sort_by) (s/enum :id :timestamp :description :source :confidence)}))

(defn ->long-id [id-type short-id]
  (id/long-id
   (id/short-id->id id-type
                    short-id
                    #(get-in @properties [:ctia :http :show]))))

(defroutes indicator-routes
  (context "/indicator" []
    :tags ["Indicator"]
    (POST "/" []
      :return StoredIndicator
      :body [indicator NewIndicator {:description "a new Indicator"}]
      :summary "Adds a new Indicator"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :create-indicator
      :login login
      (ok (flows/create-flow :realize-fn realize-indicator
                             :store-fn #(create-indicator @indicator-store %)
                             :entity-type :indicator
                             :login login
                             :entity indicator)))
    (PUT "/:id" []
      :return StoredIndicator
      :body [indicator NewIndicator {:description "an updated Indicator"}]
      :summary "Updates an Indicator"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :create-indicator
      :login login
      (ok (flows/update-flow :get-fn #(read-indicator @indicator-store %)
                             :realize-fn realize-indicator
                             :update-fn #(update-indicator @indicator-store (:id %) %)
                             :entity-type :indicator
                             :id id
                             :login login
                             :entity indicator)))
    (GET "/:id" []
      :return (s/maybe StoredIndicator)
      :summary "Gets an Indicator by ID"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-indicator
      (if-let [d (read-indicator @indicator-store id)]
        (ok d)
        (not-found)))
    (GET "/:id/sightings" []
      :return [StoredSighting]
      :path-params [id :- s/Str]
      :query [params SightingsByIndicatorQueryParams]
      :summary "Gets all Sightings associated with the Indicator"
      :capabilities #{:read-indicator :list-sightings}
      (if-let [indicator (read-indicator @indicator-store id)]
        (if-let [sightings (list-sightings-by-indicators @sighting-store [indicator] params)]
          (paginated-ok sightings)
          (not-found))
        (not-found)))
    (GET "/title/:title" []
      :return (s/maybe [StoredIndicator])
      :summary "Gets an Indicator by title"
      :query [params IndicatorsByTitleQueryParams]
      :path-params [title :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-indicator
      (paginated-ok
       (list-indicators @indicator-store {:title title} params))))
  (context "/indicators" []
    :tags ["Indicator"]
    (POST "/" []
      :return [c/ID]
      :body [indicators [NewIndicator] {:description "a list of new Indicator"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Adds a list of new Indicator"
      :capabilities :create-indicator
      :login login
      (ok (map (fn [indicator]
                 (-> (flows/create-flow :entity-type :indicator
                                        :realize-fn realize-indicator
                                        :store-fn #(create-indicator @indicator-store %)
                                        :login login
                                        :entity indicator)
                     :id))
               indicators))))
  (GET "/judgement/:id/indicators" []
    :tags ["Indicator"]
    :return (s/maybe [StoredIndicator])
    :summary "Gets all indicators referencing some judgement"
    :query [params IndicatorsListQueryParams]
    :path-params [id :- s/Str]
    :header-params [api_key :- (s/maybe s/Str)]
    :capabilities :list-indicators
    (paginated-ok
     (list-indicators @indicator-store
                      {:judgements #{{:judgement_id (->long-id :judgement id)}}}
                      params)))
  (GET "/campaign/:id/indicators" []
    :tags ["Indicator"]
    :return (s/maybe [StoredIndicator])
    :summary "Gets all indicators related to a campaign"
    :query [params IndicatorsListQueryParams]
    :path-params [id :- s/Str]
    :header-params [api_key :- (s/maybe s/Str)]
    :capabilities :list-indicators
    (paginated-ok
     (list-indicators @indicator-store
                      {:related_campaigns #{{:campaign_id (->long-id :campaign id)}}}
                      params)))
  (GET "/coa/:id/indicators" []
    :tags ["Indicator"]
    :return (s/maybe [StoredIndicator])
    :summary "Gets all indicators related to a coa"
    :query [params IndicatorsListQueryParams]
    :path-params [id :- s/Str]
    :header-params [api_key :- (s/maybe s/Str)]
    :capabilities :list-indicators
    (paginated-ok
     (list-indicators @indicator-store
                      {:related_COAs #{{:COA_id (->long-id :coa id)}}}
                      params)))
  (GET "/ttp/:id/indicators" []
    :tags ["Indicator"]
    :return (s/maybe [StoredIndicator])
    :summary "Gets all indicators indicating a TTP"
    :query [params IndicatorsListQueryParams]
    :path-params [id :- s/Str]
    :header-params [api_key :- (s/maybe s/Str)]
    :capabilities :list-indicators
    (paginated-ok
     (list-indicators @indicator-store
                      {:indicated_TTP #{{:ttp_id (->long-id :ttp id)}}}
                      params)))
  (GET "/indicator/:id/indicators" []
    :tags ["Indicator"]
    :return (s/maybe [StoredIndicator])
    :summary "Gets all indicators related to another indicator"
    :query [params IndicatorsListQueryParams]
    :path-params [id :- s/Str]
    :header-params [api_key :- (s/maybe s/Str)]
    :capabilities :list-indicators
    (paginated-ok
     (list-indicators @indicator-store
                      {:related_indicators #{{:indicator_id (->long-id :indicator id)}}}
                      params))))
