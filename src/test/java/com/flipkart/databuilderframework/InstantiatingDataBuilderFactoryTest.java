package com.flipkart.databuilderframework;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.Data;
import com.flipkart.databuilderframework.model.ExecutionGraph;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class InstantiatingDataBuilderFactoryTest {
    public static class WrongBuilder extends DataBuilder {

        public WrongBuilder(String blah) {

        }

        @Override
        public Data process(DataBuilderContext context) {
            return null;
        }
    }
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
    private DataBuilderFactory dataBuilderFactory = new InstantiatingDataBuilderFactory(dataBuilderMetadataManager);

    @Before
    public void setup() throws Exception {
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderB", null);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "X", "BuilderC", WrongBuilder.class);
    }


    @Test
    public void testCreate() throws Exception {
        try {
            Assert.assertNotNull(dataBuilderFactory.create("BuilderA"));
            dataBuilderFactory.create("BuilderB"); //Should throw
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.NO_BUILDER_FOUND_FOR_NAME == e.getErrorCode()) {
                return;
            }
        }
        fail();
     }

    @Test
    public void testFail() throws Exception {
        ExecutionGraph executionGraph = new ExecutionGraph();
        try {
            dataBuilderFactory.create("BuilderC"); //Should throw
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.INSTANTIATION_FAILURE == e.getErrorCode()) {
                return;
            }
        }
        fail();
    }
}
