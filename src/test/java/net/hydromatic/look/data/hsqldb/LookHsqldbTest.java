/*
 * Licensed to Julian Hyde under one or more contributor license
 * agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. Julian Hyde
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hydromatic.look.data.hsqldb;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Kick the tires.
 */
public class LookHsqldbTest {
  @Test public void test() throws SQLException {
    final Connection connection =
        DriverManager.getConnection(LookHsqldb.URI, LookHsqldb.USER,
            LookHsqldb.PASSWORD);
    final Statement statement = connection.createStatement();
    foo(statement.executeQuery("select * from \"all_types\""), 1, is(1));
    foo(statement.executeQuery("select * from \"nested_and_repeated\""), 1, is(2));
    foo(statement.executeQuery("select * from \"users\""), 2, is(85));
    foo(statement.executeQuery("select * from \"orders\""), 2, is(4_066));
    foo(statement.executeQuery("select * from \"order_items\""), 2, is(12_142));
    statement.close();
    connection.close();
  }

  private void foo(ResultSet resultSet, int printLimit,
      Matcher<Integer> rowCountMatcher) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();
    final int columnCount = metaData.getColumnCount();
    int row = 0;
    while (resultSet.next()) {
      if (row++ >= printLimit || printLimit < 0) {
        continue;
      }
      for (int i = 0; i < columnCount; i++) {
        if (i > 0) {
          System.out.print(", ");
        }
        System.out.print(metaData.getColumnLabel(i + 1));
        System.out.print(": ");
        System.out.print(resultSet.getObject(i + 1));
      }
      System.out.println();
    }
    assertThat(row, rowCountMatcher);
  }
}

// End LookHsqldbTest.java
