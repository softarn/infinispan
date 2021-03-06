/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.clustering.infinispan.subsystem;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * Resource description for the addressable resource /subsystem=infinispan/cache-container=X/cache=Y/store=STORE
 *
 * @author Galder Zamarreño
 */
public class RocksDBStoreConfigurationResource extends BaseStoreConfigurationResource {

    public static final PathElement ROCKSDBSTORE_PATH = PathElement.pathElement(ModelKeys.ROCKSDB_STORE);

    // attributes
    static final SimpleAttributeDefinition PATH =
            new SimpleAttributeDefinitionBuilder(ModelKeys.PATH, ModelType.STRING, true)
                    .setXmlName(Attribute.PATH.getLocalName())
                    .setAllowExpression(true)
                    .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                    .build();

    static final SimpleAttributeDefinition RELATIVE_TO =
            new SimpleAttributeDefinitionBuilder(ModelKeys.RELATIVE_TO, ModelType.STRING, true)
                    .setXmlName(Attribute.RELATIVE_TO.getLocalName())
                    .setAllowExpression(false)
                    .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                    .setDefaultValue(new ModelNode().set(ServerEnvironment.SERVER_DATA_DIR))
                    .build();

    static final SimpleAttributeDefinition BLOCK_SIZE =
            new SimpleAttributeDefinitionBuilder(ModelKeys.BLOCK_SIZE, ModelType.INT, true)
                    .setXmlName(Attribute.BLOCK_SIZE.getLocalName())
                    .setAllowExpression(true)
                    .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                    .setDefaultValue(new ModelNode().set(0))
                    .build();

    static final SimpleAttributeDefinition CACHE_SIZE =
            new SimpleAttributeDefinitionBuilder(ModelKeys.CACHE_SIZE, ModelType.LONG, true)
                    .setXmlName(Attribute.CACHE_SIZE.getLocalName())
                    .setAllowExpression(true)
                    .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                    .setDefaultValue(new ModelNode().set(0))
                    .build();

    static final SimpleAttributeDefinition CLEAR_THRESHOLD =
            new SimpleAttributeDefinitionBuilder(ModelKeys.CLEAR_THRESHOLD, ModelType.INT, true)
                    .setXmlName(Attribute.CLEAR_THRESHOLD.getLocalName())
                    .setAllowExpression(true)
                    .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                    .setDefaultValue(new ModelNode().set(10000))
                    .build();

    static final AttributeDefinition[] ROCKSDB_STORE_ATTRIBUTES = {PATH, BLOCK_SIZE, CACHE_SIZE, CLEAR_THRESHOLD};

    static final SimpleAttributeDefinition NAME =
            new SimpleAttributeDefinitionBuilder(BaseStoreConfigurationResource.NAME)
                    .setDefaultValue(new ModelNode().set(ModelKeys.ROCKSDB_STORE_NAME))
                    .build();

    public RocksDBStoreConfigurationResource(CacheConfigurationResource parent) {
        super(ROCKSDBSTORE_PATH, ModelKeys.ROCKSDB_STORE, parent, ROCKSDB_STORE_ATTRIBUTES);
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        super.registerChildren(resourceRegistration);
        // child resources
        resourceRegistration.registerSubModel(new RocksDBExpirationConfigurationResource(resource));
        resourceRegistration.registerSubModel(new RocksDBCompressionConfigurationResource(resource));
    }

}
