package allst.utils.parse;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class getStmtTables {


    public static void main(String args[]) throws IOException {
//		long t = System.currentTimeMillis( );
//
//
//
//		if ( args.length != 1 )
//		{
//			System.out.println( "Usage: java allst.utils.parse.getStmtTables sqlfile.sql" );
//			return;
//		}
//		File file = new File( args[0] );
//		if ( !file.exists( ) )
//		{
//			System.out.println( "File not exists:" + args[0] );
//			return;
//		}
//
//		EDbVendor dbVendor = EDbVendor.dbvoracle;
//		String msg = "Please select SQL dialect: 1: SQL Server, 2: Oralce, 3: MySQL, 4: DB2, 5: PostGRESQL, 6: Teradta, default is 2: Oracle";
//		System.out.println( msg );
//
//		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
//		try
//		{
//			int db = Integer.parseInt( br.readLine( ) );
//			if ( db == 1 )
//			{
//				dbVendor = EDbVendor.dbvmssql;
//			}
//			else if ( db == 2 )
//			{
//				dbVendor = EDbVendor.dbvoracle;
//			}
//			else if ( db == 3 )
//			{
//				dbVendor = EDbVendor.dbvmysql;
//			}
//			else if ( db == 4 )
//			{
//				dbVendor = EDbVendor.dbvdb2;
//			}
//			else if ( db == 5 )
//			{
//				dbVendor = EDbVendor.dbvpostgresql;
//			}
//			else if ( db == 6 )
//			{
//				dbVendor = EDbVendor.dbvteradata;
//			}
//		}
//		catch ( IOException i )
//		{
//		}
//		catch ( NumberFormatException numberFormatException )
//		{
//		}
//
//		System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

//		String sql = "";


        String sqlFile = args[0];
        InputStream sqlFileIn = new FileInputStream(sqlFile);
        StringBuffer sqlSb = new StringBuffer();
        byte[] buff = new byte[1024];
        int byteRead = 0;
        while ((byteRead = sqlFileIn.read(buff)) != -1) {
            sqlSb.append(new String(buff, 0, byteRead, "utf-8"));
        }
        sqlFileIn.close();
        String sql = sqlSb.toString();
        String[] strs = sql.split("\\;");


        EDbVendor dbVendor = gudusoft.gsqlparser.EDbVendor.dbvdb2;


        for (String s : strs) {
            TGSqlParser sqlparser = new TGSqlParser(dbVendor);
            sqlparser.sqltext = s;
            int ret = sqlparser.parse();
            if (ret != 0) {
//				System.err.println(sqlparser.getErrormessage());
                new getStmtTables(s, EDbVendor.dbvhive, sqlFile);
            } else {
                new getStmtTables(s, dbVendor, sqlFile);
            }
        }


//		new allst.utils.parse.getStmtTables( sql, dbVendor );

    }

//    public allst.utils.parse.getStmtTables( File file, EDbVendor dbVendor )
//    {
//        TGSqlParser sqlparser = new TGSqlParser( dbVendor );
//        sqlparser.sqlfilename = file.getAbsolutePath( );
//        int ret = sqlparser.parse( );
//        if ( ret != 0 )
//        {
//            System.err.println( sqlparser.getErrormessage( ) );
//        }
//        else
//        {
//            for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
//            {
//                if ( sqlparser.sqlstatements.get( k ) instanceof TCustomSqlStatement )
//                {
//                    analyzeStatement( (TCustomSqlStatement) sqlparser.sqlstatements.get( k ) );
//                }
//            }
//        }
//    }

    public getStmtTables(String sql, EDbVendor dbVendor, String FileName) {
        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqltext = sql;
        int ret = sqlparser.parse();
        if (ret != 0) {
            System.err.println(sqlparser.getErrormessage());
        } else {
            for (int k = 0; k < sqlparser.sqlstatements.size(); k++) {
                if (sqlparser.sqlstatements.get(k) instanceof TCustomSqlStatement) {
                    analyzeStatement((TCustomSqlStatement) sqlparser.sqlstatements.get(k), FileName);
                }
            }
        }
    }

    public void analyzeStatement(TCustomSqlStatement stmt, String FileName) {
        if (stmt instanceof TSelectSqlStatement) {
            /**
             * 解析sql脚本的select开头的语句
             */
//            Set<String> tables = analyzeSelectStatement( (TSelectSqlStatement) stmt );
//            if ( !tables.isEmpty( ) )
//            {
//                System.out.print( "Source: " );
//                String[] tableArray = tables.toArray( new String[0] );
//                for ( int i = 0; i < tableArray.length; i++ )
//                {
//                    System.out.print( tableArray[i] );
//                    if ( i < tableArray.length - 1 )
//                    {
//                        System.out.print( ", " );
//                    }
//                }
//                System.out.println( );
//            }
        } else if (stmt instanceof TInsertSqlStatement) {
            TInsertSqlStatement insert = (TInsertSqlStatement) stmt;
            analyzeInsertStatement(insert, FileName);
        } else if (stmt instanceof TUpdateSqlStatement) {
            TUpdateSqlStatement update = (TUpdateSqlStatement) stmt;
            analyzeUpdateStatement(update, FileName);
        }
//        else if ( stmt instanceof TDeleteSqlStatement )
//        {
////			TDeleteSqlStatement delete = (TDeleteSqlStatement) stmt;
////			analyzeDeleteStatement( delete );
//        }
//        else if ( stmt instanceof TDropTableSqlStatement )
//        {
//            TDropTableSqlStatement drop = (TDropTableSqlStatement) stmt;
//            if ( drop.getTableName( ) != null )
//            {
//                System.out.println( "Target: "
//                        + drop.getTableName( ).toString( ) );
//            }
//        }
//        else if ( stmt instanceof TAlterTableStatement )
//        {
//            TAlterTableStatement alter = (TAlterTableStatement) stmt;
//            if ( alter.getTableName( ) != null )
//            {
//                System.out.println( "Target: "
//                        + alter.getTableName( ).toString( ) );
//            }
//        }
        else if (stmt instanceof TMergeSqlStatement) {
            TMergeSqlStatement merge = (TMergeSqlStatement) stmt;
            analyzeMergeStatement(merge, FileName);
        }

//        System.out.println( );
    }


    /**
     * 已修改,//已添加时间戳//已添加文件名
     *
     * @param merge
     */
    private void analyzeMergeStatement(TMergeSqlStatement merge, String FileName) {
        String TargetTable = new String();
        String out;
        if (merge.getTargetTable() != null) {
            TargetTable = analyzeTableout(merge.getTargetTable().getFullName());

//			System.out.println( "Target: "
//					+ merge.getTargetTable( ).getFullName( ) );
        }
        Set<String> tables = new LinkedHashSet<String>();
        if (merge.getUsingTable() != null) {
            if (merge.getUsingTable().isBaseTable())
                tables.add(merge.getUsingTable().getFullName());
            else if (merge.getUsingTable().getSubquery() != null) {
                tables.addAll(analyzeSelectStatement(merge.getUsingTable()
                        .getSubquery()));
            }
            if (merge.getCondition() != null) {
                new tablesInExpr(this, merge.getCondition(), tables).searchTable();
            }
            if (merge.getWhenClauses() != null) {
                for (int i = 0; i < merge.getWhenClauses().size(); i++) {
                    TMergeWhenClause when = merge.getWhenClauses()
                            .getElement(i);
                    if (when.getCondition() != null) {
                        new tablesInExpr(this, when.getCondition(), tables).searchTable();
                    }
                    if (when.getInsertClause() != null
                            && when.getInsertClause().getValuelist() != null) {
                        for (int j = 0; j < when.getInsertClause()
                                .getValuelist()
                                .size(); j++) {
                            TResultColumn field = when.getInsertClause()
                                    .getValuelist()
                                    .getResultColumn(j);
                            if (field.getExpr().getExpressionType() == EExpressionType.subquery_t) {
                                tables.addAll(analyzeSelectStatement(field.getExpr()
                                        .getSubQuery()));
                            }
                        }
                    }
                    if (when.getUpdateClause() != null) {
                        if (when.getUpdateClause().getUpdateWhereClause() != null) {
                            new tablesInExpr(this, when.getUpdateClause()
                                    .getUpdateWhereClause(), tables).searchTable();
                        }
                        if (when.getUpdateClause().getUpdateColumnList() != null) {
                            for (int j = 0; j < when.getUpdateClause()
                                    .getUpdateColumnList()
                                    .size(); j++) {
                                TResultColumn field = when.getUpdateClause()
                                        .getUpdateColumnList()
                                        .getResultColumn(j);
                                if (field.getExpr()
                                        .getRightOperand()
                                        .getExpressionType() == EExpressionType.subquery_t) {
                                    tables.addAll(analyzeSelectStatement(field.getExpr()
                                            .getRightOperand()
                                            .getSubQuery()));
                                }
                            }
                        }
                    }
                }
            }
            if (!tables.isEmpty()) {
//				System.out.print( "Source: " );
                String[] tableArray = tables.toArray(new String[0]);
                for (int i = 0; i < tableArray.length; i++) {
                    TableNameStruct TableNameStruct = new TableNameStruct();
                    TableNameStruct.SqlFileName = FileName;
                    out = TargetTable + "," + analyzeTableout(tableArray[i]) + "," + TableNameStruct.SqlFileName + "," + TableNameStruct.time;
                    System.out.println(out);
//					if ( i < tableArray.length - 1 )
//					{
//						System.out.print( ", " );
//					}
                }
//				System.out.println( );
            }
        }
    }

    /**
     * 未修改,可能有多个表删除？
     * @param delete
     */
//	private void analyzeDeleteStatement( TDeleteSqlStatement delete )
//	{
//		Set<String> tables = new LinkedHashSet<String>( );
//
//		if ( delete.getTargetTable( ) != null )
//		{
//			System.out.println( "Target: "
//					+ delete.getTargetTable( ).getFullName( ) );
//			tables.add( delete.getTargetTable( ).getFullName( ) );
//		}
//
//		if ( delete.getResultColumnList( ) != null )
//		{
//			for ( int i = 0; i < delete.getResultColumnList( ).size( ); i++ )
//			{
//				TResultColumn field = delete.getResultColumnList( )
//						.getResultColumn( i );
//				if ( field.getExpr( ).getRightOperand( ) != null
//						&& field.getExpr( )
//								.getRightOperand( )
//								.getExpressionType( ) == EExpressionType.subquery_t )
//				{
//					tables.addAll( analyzeSelectStatement( field.getExpr( )
//							.getRightOperand( )
//							.getSubQuery( ) ) );
//				}
//			}
//		}
//
//		if ( delete.joins != null )
//		{
//			for ( int i = 0; i < delete.joins.size( ); i++ )
//			{
//				TJoin join = delete.joins.getJoin( i );
//				if ( join.getTable( ).isBaseTable( ) )
//					tables.add( join.getTable( ).getFullName( ) );
//				TJoinItemList items = join.getJoinItems( );
//				if ( items != null )
//				{
//					for ( int j = 0; j < items.size( ); j++ )
//					{
//						TJoinItem item = items.getJoinItem( j );
//						if ( item.getTable( ).isBaseTable( ) )
//							tables.add( item.getTable( ).getFullName( ) );
//						if ( item.getOnCondition( ) != null )
//						{
//							new tablesInExpr( this,
//									item.getOnCondition( ),
//									tables ).searchTable( );
//						}
//					}
//				}
//			}
//		}
//
//		if ( delete.getWhereClause( ) != null
//				&& delete.getWhereClause( ).getCondition( ) != null )
//		{
//
//			new tablesInExpr( this,
//					delete.getWhereClause( ).getCondition( ),
//					tables ).searchTable( );
//		}
//		if ( !tables.isEmpty( ) )
//		{
//			System.out.print( "Source: " );
//			String[] tableArray = tables.toArray( new String[0] );
//			for ( int i = 0; i < tableArray.length; i++ )
//			{
//				System.out.print( tableArray[i] );
//				if ( i < tableArray.length - 1 )
//				{
//					System.out.print( ", " );
//				}
//			}
//			System.out.println( );
//		}
//	}

    /**
     * 已修改,//已添加时间戳//已添加文件名
     *
     * @param update
     */
    private void analyzeUpdateStatement(TUpdateSqlStatement update, String FileName) {
        String TargetTable = new String();
        String out;

        if (update.getTargetTable() != null) {

            TargetTable = analyzeTableout(update.getTargetTable().getFullName());
//			System.out.println(TargetTable);
//			System.out.println( "Target: "
//					+ update.getTargetTable( ).getFullName( ) );
        }
        Set<String> tables = new LinkedHashSet<String>();

        if (update.getResultColumnList() != null) {
            for (int i = 0; i < update.getResultColumnList().size(); i++) {
                TResultColumn field = update.getResultColumnList()
                        .getResultColumn(i);
                if (field.getExpr().getRightOperand() != null
                        && field.getExpr()
                        .getRightOperand()
                        .getExpressionType() == EExpressionType.subquery_t) {
                    tables.addAll(analyzeSelectStatement(field.getExpr()
                            .getRightOperand()
                            .getSubQuery()));
                }
            }
        }

        if (update.joins != null) {
            for (int i = 0; i < update.joins.size(); i++) {
                TJoin join = update.joins.getJoin(i);
                if (join.getTable().isBaseTable())
                    tables.add(join.getTable().getFullName());
                TJoinItemList items = join.getJoinItems();
                if (items != null) {
                    for (int j = 0; j < items.size(); j++) {
                        TJoinItem item = items.getJoinItem(j);
                        if (item.getTable().isBaseTable())
                            tables.add(item.getTable().getFullName());
                        if (item.getOnCondition() != null) {
                            new tablesInExpr(this,
                                    item.getOnCondition(),
                                    tables).searchTable();
                        }
                    }
                }
            }
        }

        if (update.getWhereClause() != null
                && update.getWhereClause().getCondition() != null) {

            new tablesInExpr(this,
                    update.getWhereClause().getCondition(),
                    tables).searchTable();
        }

//		源表
        if (!tables.isEmpty()) {
//			System.out.print( "Source: " );
            String[] tableArray = tables.toArray(new String[0]);
            for (int i = 0; i < tableArray.length; i++) {
                TableNameStruct TableNameStruct = new TableNameStruct();
                TableNameStruct.SqlFileName = FileName;

                out = TargetTable + "," + analyzeTableout(tableArray[i]) + "," + TableNameStruct.SqlFileName + "," + TableNameStruct.time;
                System.out.println(out);

//				System.out.print( tableArray[i] );
//				if ( i < tableArray.length - 1 )
//				{
//					System.out.print( ", " );
//				}
            }
//			System.out.println( );
        }
    }


    /**
     * 已修改完成 //已添加时间戳//已添加文件名
     *
     * @param insert
     */
    private void analyzeInsertStatement(TInsertSqlStatement insert, String FileName) {
        Set<String> targets = new LinkedHashSet<String>();
        String TargetTable = new String();
        String out;

        if (insert.getTargetTable() != null) {
            if (insert.getTargetTable() != null) {
                if (insert.getTargetTable().isBaseTable()
                        && !targets.contains(insert.getTargetTable()
                        .getFullName()))
                    targets.add(insert.getTargetTable().getFullName());
                else if (insert.getTargetTable().getSubquery() != null) {
                    targets.addAll(analyzeSelectStatement(insert.getTargetTable()
                            .getSubquery()));
                }
            }
        }
        if (insert.getInsertIntoValues() != null) {
            for (int i = 0; i < insert.getInsertIntoValues().size(); i++) {
                TInsertIntoValue intoValue = insert.getInsertIntoValues()
                        .getElement(i);
                if (intoValue.getTable() != null
                        && intoValue.getTable().isBaseTable()
                        && !targets.contains(intoValue.getTable()
                        .getFullName()))
                    targets.add(intoValue.getTable().getFullName());
            }

        }
        if (insert.getInsertConditions() != null) {
            for (int i = 0; i < insert.getInsertConditions().size(); i++) {
                TInsertCondition intoCondition = insert.getInsertConditions()
                        .getElement(i);
                if (intoCondition.getInsertIntoValues() != null) {
                    for (int j = 0; j < intoCondition.getInsertIntoValues()
                            .size(); j++) {
                        TInsertIntoValue intoValue = intoCondition.getInsertIntoValues()
                                .getElement(j);
                        if (intoValue.getTable() != null
                                && intoValue.getTable().isBaseTable()
                                && !targets.contains(intoValue.getTable()
                                .getFullName()))
                            targets.add(intoValue.getTable().getFullName());
                    }
                }
            }
        }

        if (!targets.isEmpty()) {
//			System.out.print( "Target: " );
            String[] tableArray = targets.toArray(new String[0]);


//			目标表
            for (int i = 0; i < tableArray.length; i++) {
//				System.out.print( tableArray[i] );


                /**
                 * 修改处，调用该函数可返回
                 *
                 */

                TargetTable = analyzeTableout(tableArray[i]);

//
//				if ( i < tableArray.length - 1 )
//				{
//					System.out.print( ", " );
//				}
            }
//			System.out.println( );
        }

        Set<String> sources = new LinkedHashSet<String>();

        if (insert.getValues() != null) {
            for (int i = 0; i < insert.getValues().size(); i++) {
                TMultiTarget multiTarget = insert.getValues()
                        .getMultiTarget(i);
                if (multiTarget.getSubQuery() != null) {
                    sources.addAll(analyzeSelectStatement(multiTarget.getSubQuery()));
                }

                for (int j = 0; j < multiTarget.getColumnList().size(); j++) {
                    TResultColumn field = multiTarget.getColumnList()
                            .getResultColumn(j);
                    if (field.getExpr().getExpressionType() == EExpressionType.subquery_t) {
                        sources.addAll(analyzeSelectStatement(field.getExpr()
                                .getSubQuery()));
                    }
                }
            }
        }

        if (insert.getSubQuery() != null) {
            sources.addAll(analyzeSelectStatement(insert.getSubQuery()));
        }

        if (!sources.isEmpty()) {

            /**
             * source 打印注释的都是原来需要的
             *
             */
//			System.out.print( "Source: " );
            String[] tableArray = sources.toArray(new String[0]);
            for (int i = 0; i < tableArray.length; i++) {
//				System.out.print( tableArray[i] );
                TableNameStruct TableNameStruct = new TableNameStruct();
                TableNameStruct.SqlFileName = FileName;

                out = TargetTable + "," + analyzeTableout(tableArray[i]) + "," + TableNameStruct.SqlFileName + "," + TableNameStruct.time;
                System.out.println(out);
//				System.out.println( );
//				if ( i < tableArray.length - 1 )
//				{
//					System.out.print( ", " );
//				}
            }
//			System.out.println( );
        }
    }

    class tablesInExpr implements IExpressionVisitor {

        private Set<String> tables;
        private TExpression expr;
        private getStmtTables impact;

        public tablesInExpr(getStmtTables impact, TExpression expr,
                            Set<String> tables) {
            this.impact = impact;
            this.expr = expr;
            this.tables = tables;
        }

        private void addColumnToList(TParseTreeNodeList list) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    List<TExpression> exprList = new ArrayList<TExpression>();
                    Object element = list.getElement(i);

                    if (element instanceof TGroupByItem) {
                        exprList.add(((TGroupByItem) element).getExpr());
                    }
                    if (element instanceof TOrderByItem) {
                        exprList.add(((TOrderByItem) element).getSortKey());
                    } else if (element instanceof TExpression) {
                        exprList.add((TExpression) element);
                    } else if (element instanceof TWhenClauseItem) {
                        exprList.add(((TWhenClauseItem) element).getComparison_expr());
                        exprList.add(((TWhenClauseItem) element).getReturn_expr());
                    }

                    for (TExpression expr : exprList) {
                        expr.inOrderTraverse(this);
                    }
                }
            }
        }

        public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode) {
            TExpression lcexpr = (TExpression) pNode;
            if (lcexpr.getExpressionType() == EExpressionType.simple_object_name_t) {

            } else if (lcexpr.getExpressionType() == EExpressionType.between_t) {

            } else if (lcexpr.getExpressionType() == EExpressionType.function_t) {
                TFunctionCall func = (TFunctionCall) lcexpr.getFunctionCall();
                if (func.getFunctionType() == EFunctionType.trim_t) {
                    TTrimArgument args = func.getTrimArgument();
                    TExpression expr = args.getStringExpression();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                    expr = args.getTrimCharacter();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                } else if (func.getFunctionType() == EFunctionType.cast_t) {
                    TExpression expr = func.getExpr1();
                    if (expr != null
                            && !expr.toString().trim().equals("*")
                            || func.getFunctionType() == EFunctionType.extract_t) {
                        expr.inOrderTraverse(this);
                    }
                } else if (func.getFunctionType() == EFunctionType.convert_t) {
                    TExpression expr = func.getExpr1();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                    expr = func.getExpr2();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                } else if (func.getFunctionType() == EFunctionType.contains_t
                        || func.getFunctionType() == EFunctionType.freetext_t) {
                    TExpression expr = func.getExpr1();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                    TInExpr inExpr = func.getInExpr();
                    if (inExpr.getExprList() != null) {
                        for (int k = 0; k < inExpr.getExprList().size(); k++) {
                            expr = inExpr.getExprList().getExpression(k);
                            if (expr.toString().trim().equals("*"))
                                continue;
                            expr.inOrderTraverse(this);
                        }
                        if (expr != null
                                && !expr.toString().trim().equals("*")) {
                            expr.inOrderTraverse(this);
                        }
                    }
                    expr = inExpr.getFunc_expr();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                } else if (func.getFunctionType() == EFunctionType.extractxml_t) {
                    TExpression expr = func.getXMLType_Instance();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                    expr = func.getXPath_String();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                    expr = func.getNamespace_String();
                    if (expr != null && !expr.toString().trim().equals("*")) {
                        expr.inOrderTraverse(this);
                    }
                }

                if (func.getFunctionType() == EFunctionType.rank_t) {
                    TOrderByItemList orderByList = func.getOrderByList();
                    for (int k = 0; k < orderByList.size(); k++) {
                        TExpression expr = orderByList.getOrderByItem(k)
                                .getSortKey();
                        if (expr.toString().trim().equals("*"))
                            continue;
                        expr.inOrderTraverse(this);
                    }
                } else if (func.getArgs() != null) {
                    for (int k = 0; k < func.getArgs().size(); k++) {
                        TExpression expr = func.getArgs().getExpression(k);
                        if (expr.toString().trim().equals("*"))
                            continue;
                        expr.inOrderTraverse(this);
                    }
                }
                if (func.getAnalyticFunction() != null) {
                    TParseTreeNodeList list = func.getAnalyticFunction()
                            .getPartitionBy_ExprList();
                    addColumnToList(list);

                    if (func.getAnalyticFunction().getOrderBy() != null) {
                        list = func.getAnalyticFunction()
                                .getOrderBy()
                                .getItems();
                        addColumnToList(list);
                    }
                }

            } else if (lcexpr.getExpressionType() == EExpressionType.subquery_t) {
                tables.addAll(impact.analyzeSelectStatement(lcexpr.getSubQuery()));
            } else if (lcexpr.getExpressionType() == EExpressionType.case_t) {
                TCaseExpression expr = lcexpr.getCaseExpression();
                TExpression conditionExpr = expr.getInput_expr();
                if (conditionExpr != null) {
                    conditionExpr.inOrderTraverse(this);
                }
                TExpression defaultExpr = expr.getElse_expr();
                if (defaultExpr != null) {
                    defaultExpr.inOrderTraverse(this);
                }
                TWhenClauseItemList list = expr.getWhenClauseItemList();
                addColumnToList(list);
            }
            return true;
        }

        public void searchTable() {
            this.expr.inOrderTraverse(this);
        }
    }

    private Set<String> analyzeSelectStatement(TSelectSqlStatement stmt) {

        Set<String> tables = new LinkedHashSet<String>();
        if (stmt.getSetOperator() != TSelectSqlStatement.setOperator_none) {
            tables.addAll(analyzeSelectStatement(stmt.getLeftStmt()));
            tables.addAll(analyzeSelectStatement(stmt.getRightStmt()));
        } else {
            for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
                TResultColumn field = stmt.getResultColumnList()
                        .getResultColumn(i);
                if (field.getExpr().getExpressionType() == EExpressionType.subquery_t) {
                    tables.addAll(analyzeSelectStatement(field.getExpr()
                            .getSubQuery()));
                }
            }

            if (stmt.getWhereClause() != null
                    && stmt.getWhereClause().getCondition() != null) {
                new tablesInExpr(this,
                        stmt.getWhereClause().getCondition(),
                        tables).searchTable();
            }

            if (stmt.joins != null) {
                for (int i = 0; i < stmt.joins.size(); i++) {
                    TJoin join = stmt.joins.getJoin(i);
                    if (join.getTable().isBaseTable())
                        tables.add(join.getTable().getFullName());
                    else if (join.getTable().getSubquery() != null) {
                        tables.addAll(analyzeSelectStatement(join.getTable()
                                .getSubquery()));
                    }
                    TJoinItemList items = join.getJoinItems();
                    if (items != null) {
                        for (int j = 0; j < items.size(); j++) {
                            TJoinItem item = items.getJoinItem(j);
                            if (item.getTable().isBaseTable())
                                tables.add(item.getTable().getFullName());
                            else if (item.getTable().getSubquery() != null) {
                                tables.addAll(analyzeSelectStatement(item.getTable()
                                        .getSubquery()));
                            }
                            if (item.getOnCondition() != null) {
                                new tablesInExpr(this,
                                        item.getOnCondition(),
                                        tables).searchTable();
                            }
                        }
                    }
                }
            }

            if (stmt.getTargetTable() != null) {
                if (stmt.getTargetTable().isBaseTable())
                    tables.add(stmt.getTargetTable().getFullName());
                else if (stmt.getTargetTable().getSubquery() != null) {
                    tables.addAll(analyzeSelectStatement(stmt.getTargetTable()
                            .getSubquery()));
                }
            }

            if (stmt.tables != null) {
                for (int i = 0; i < stmt.tables.size(); i++) {
                    TTable table = stmt.tables.getTable(i);
                    if (table.isBaseTable())
                        tables.add(table.getFullName());
                    else if (table.getSubquery() != null) {
                        tables.addAll(analyzeSelectStatement(table.getSubquery()));
                    }
                }

            }
        }

        return tables;
    }

    private String analyzeTableout(String tablename) {

        String[] dbnamesplit = tablename.split("\\.");
        TableNameStruct TableNameStruct = new TableNameStruct();
        if (dbnamesplit.length - 1 == 2) {
            TableNameStruct.tablename = dbnamesplit[2];
            TableNameStruct.database = dbnamesplit[1];
            TableNameStruct.catalog = dbnamesplit[0];
        } else if (dbnamesplit.length - 1 == 1) {
            TableNameStruct.tablename = dbnamesplit[1];
            TableNameStruct.database = dbnamesplit[0];
        } else if (dbnamesplit.length - 1 == 0) {
            TableNameStruct.tablename = dbnamesplit[0];

        }
        String out = TableNameStruct.catalog + "," + TableNameStruct.database + "," + TableNameStruct.tablename;
//		System.out.println(out);
        return out;
    }

    class TableNameStruct {
        private String catalog;

        private String database;

        private String tablename;

        private Timestamp time = new Timestamp(System.currentTimeMillis());

        private String SqlFileName;


    }
}