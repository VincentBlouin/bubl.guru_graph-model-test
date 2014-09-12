/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.graph.schema.SchemaOperator;

import java.net.URI;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class SchemaOperatorTest extends AdaptableGraphComponentTest {

    @Test
    public void can_add_property() {
        GraphElementOperator property = createSchema().addProperty();
        assertThat(
                property.uri(),
                is(notNullValue())
        );
    }

    @Test
    public void properties_are_empty_when_no_properties() {
        SchemaOperator schemaOperator = createSchema();
        assertTrue(
                schemaOperator.getProperties().isEmpty()
        );
    }

    @Test
    public void can_get_properties() {
        SchemaOperator schemaOperator = createSchema();
        GraphElementOperator property1 = schemaOperator.addProperty();
        GraphElementOperator property2 = schemaOperator.addProperty();
        Collection<? extends GraphElement> properties = schemaOperator.getProperties().values();
        assertTrue(
                properties.contains(property1)
        );
        assertTrue(
                properties.contains(property2)
        );
    }

    @Test
    public void created_properties_have_created_and_last_modified_date() {
        SchemaOperator schemaOperator = createSchema();
        GraphElementOperator property = schemaOperator.addProperty();
        assertThat(
                property.creationDate(),
                is(notNullValue())
        );
    }

    @Test
    public void cannot_remove_a_schema() {
        SchemaOperator schemaOperator = createSchema();
        URI propertyUri = schemaOperator.addProperty().uri();
        assertTrue(
                userGraph.haveElementWithId(
                        propertyUri
                )
        );
        schemaOperator.remove();
        assertTrue(
                userGraph.haveElementWithId(
                        propertyUri
                )
        );
    }

    @Test
    public void can_remove_a_schema_property() {
        SchemaOperator schemaOperator = createSchema();
        GraphElementOperator property = schemaOperator.addProperty();
        URI propertyUri = property.uri();
        assertTrue(
                userGraph.haveElementWithId(
                        propertyUri
                )
        );
        property.remove();
        assertFalse(
                userGraph.haveElementWithId(
                        propertyUri
                )
        );
    }
    private SchemaOperator createSchema() {
        return userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
    }
}
