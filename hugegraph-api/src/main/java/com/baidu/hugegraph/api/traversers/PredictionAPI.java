/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.api.traversers;

import static com.baidu.hugegraph.traversal.algorithm.HugeTraverser.DEFAULT_ELEMENTS_LIMIT;
import static com.baidu.hugegraph.traversal.algorithm.HugeTraverser.DEFAULT_MAX_DEGREE;

import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;

import com.baidu.hugegraph.HugeGraph;
import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.api.graph.EdgeAPI;
import com.baidu.hugegraph.api.graph.VertexAPI;
import com.baidu.hugegraph.backend.id.Id;
import com.baidu.hugegraph.core.GraphManager;
import com.baidu.hugegraph.server.RestServer;
import com.baidu.hugegraph.traversal.algorithm.PredictionTraverser;
import com.baidu.hugegraph.type.define.Directions;
import com.baidu.hugegraph.util.JsonUtil;
import com.baidu.hugegraph.util.Log;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;

/**
 * This API include similar prediction algorithms, now include:
 *  - Adamic Adar
 *  - Resource Allocation
 */
@Path("graphs/{graph}/traversers/")
@Singleton
public class PredictionAPI extends API {

    private static final Logger LOG = Log.logger(RestServer.class);

    @GET
    @Timed
    @Path("adamicadar")
    @Produces(APPLICATION_JSON_WITH_CHARSET)
    public String get(@Context GraphManager manager,
                      @PathParam("graph") String graph,
                      @QueryParam("vertex") String current,
                      @QueryParam("other") String other,
                      @QueryParam("direction") String direction,
                      @QueryParam("label") String edgeLabel,
                      @QueryParam("max_degree")
                      @DefaultValue(DEFAULT_MAX_DEGREE) long maxDegree,
                      @QueryParam("limit")
                      @DefaultValue(DEFAULT_ELEMENTS_LIMIT) long limit) {
        LOG.debug("Graph [{}] get adamic adar between '{}' and '{}' with " +
                  "direction {}, edge label {}, max degree '{}' and limit '{}'",
                  graph, current, other, direction, edgeLabel, maxDegree, limit);

        Id sourceId = VertexAPI.checkAndParseVertexId(current);
        Id targetId = VertexAPI.checkAndParseVertexId(other);
        Directions dir = Directions.convert(EdgeAPI.parseDirection(direction));

        HugeGraph g = graph(manager, graph);
        PredictionTraverser traverser = new PredictionTraverser(g);
        double score = traverser.adamicAdar(sourceId, targetId, dir,
                                            edgeLabel, maxDegree, limit);
        return JsonUtil.toJson(ImmutableMap.of("adamic_adar", score));
    }

    @GET
    @Timed
    @Path("resourceallocation")
    @Produces(APPLICATION_JSON_WITH_CHARSET)
    public String create(@Context GraphManager manager,
                         @PathParam("graph") String graph,
                         @QueryParam("vertex") String current,
                         @QueryParam("other") String other,
                         @QueryParam("direction") String direction,
                         @QueryParam("label") String edgeLabel,
                         @QueryParam("max_degree")
                         @DefaultValue(DEFAULT_MAX_DEGREE) long maxDegree,
                         @QueryParam("limit")
                         @DefaultValue(DEFAULT_ELEMENTS_LIMIT) long limit) {
        LOG.debug("Graph [{}] get resource allocation between '{}' and '{}' " +
                  "with direction {}, edge label {}, max degree '{}' and " +
                  "limit '{}'", graph, current, other, direction, edgeLabel,
                  maxDegree, limit);

        Id sourceId = VertexAPI.checkAndParseVertexId(current);
        Id targetId = VertexAPI.checkAndParseVertexId(other);
        Directions dir = Directions.convert(EdgeAPI.parseDirection(direction));

        HugeGraph g = graph(manager, graph);
        PredictionTraverser traverser = new PredictionTraverser(g);
        double score = traverser.resourceAllocation(sourceId, targetId, dir,
                                                    edgeLabel, maxDegree,
                                                    limit);
        return JsonUtil.toJson(ImmutableMap.of("resource_allocation", score));
    }
}
