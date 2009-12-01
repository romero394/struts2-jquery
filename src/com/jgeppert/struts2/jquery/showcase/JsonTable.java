/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jgeppert.struts2.jquery.showcase;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.SessionAware;

import com.jgeppert.struts2.jquery.showcase.model.Customer;
import com.jgeppert.struts2.jquery.showcase.model.CustomerDAO;
import com.opensymphony.xwork2.ActionSupport;


@ParentPackage( value = "showcase")
public class JsonTable extends ActionSupport implements SessionAware {
    
	private static final long serialVersionUID = 5078264277068533593L;
	private static final Log    log               = LogFactory.getLog(JsonTable.class);

	private List<Customer> gridModel;
  private List<Customer> myCustomers;
	private Integer rows = 0;
	private Integer page = 0;
	private Integer total = 0;
	private Integer record = 0;
	private String sord;
	private String sidx;
	private String searchField;
	private String searchString;
	private String searchOper;
	private boolean loadonce = false;
	private Map<String, Object> session;

	@Actions({@Action(value="/jsontable", results={@Result(name="success",type="json")}) })
    public String execute() {
      
      log.info("build json table");
      log.debug("Page :"+getPage());
      log.debug("Rows :"+getRows());
      log.debug("Sorting Order :"+getSord());
      log.debug("Index Row :"+getSidx());
      log.debug("Search :"+searchField+" "+searchOper+" "+searchString);
      
      Object list = session.get("mylist");
      if(list != null)
      {
        myCustomers = (List<Customer>) list; 
      }
      else
      {
        log.debug("Build new List");
        myCustomers = CustomerDAO.buildList();
      }
      
      if(getSord() != null && getSord().equalsIgnoreCase("asc"))
      {
        Collections.sort(myCustomers);
      }
      if(getSord() != null && getSord().equalsIgnoreCase("desc"))
      {
        Collections.sort(myCustomers);
        Collections.reverse(myCustomers);
      }
      
      setRecord(CustomerDAO.getCustomersCount(myCustomers));

      int to = (getRows() * getPage());
      int from = to - getRows();
      
      if(to > getRecord())
    	  to = getRecord();
      
      if(loadonce)
      {
        setGridModel(myCustomers);
      }
      else
      {
        setGridModel(CustomerDAO.getCustomers(myCustomers,from,to));
      }
      
      
      setTotal((int)Math.ceil((double)getRecord()/(double)getRows()));
      
      session.put("mylist", myCustomers);
      
      return SUCCESS;
    }

    public String getJSON(){
    	return execute();
    }
	/**
	 * @return how many rows we want to have into the grid 
	 */
	public Integer getRows() {
		return rows;
	}


	/**
	 * @param rows how many rows we want to have into the grid 
	 */
	public void setRows(Integer rows) {
		this.rows = rows;
	}


	/**
	 * @return current page of the query
	 */
	public Integer getPage() {
		return page;
	}


	/**
	 * @param page current page of the query
	 */
	public void setPage(Integer page) {
		this.page = page;
	}


	/**
	 * @return 	total pages for the query
	 */
	public Integer getTotal() {
		return total;
	}


	/**
	 * @param total total pages for the query
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}


	/**
	 * @return total number of records for the query. e.g. select count(*) from table
	 */
	public Integer getRecord() {
		return record;
	}


	/**
	 * @param record total number of records for the query. e.g. select count(*) from table
	 */
	public void setRecord(Integer record) {

		this.record = record;

		if( this.record > 0 && this.rows > 0) { 
            this.total =  (int)Math.ceil((double)this.record / (double)this.rows);
		} else { 
			this.total = 0; 
		} 
	}


	/**
	 * @return an collection that contains the actual data
	 */
	public List<Customer> getGridModel() {
		return gridModel;
	}


	/**
	 * @param gridModel an collection that contains the actual data
	 */
	public void setGridModel(List<Customer> gridModel) {
		this.gridModel = gridModel;
	}


	/**
	 * @return sorting order
	 */
	public String getSord() {
		return sord;
	}


	/**
	 * @param sord sorting order
	 */
	public void setSord(String sord) {
		this.sord = sord;
	}


	/**
	 * @return get index row - i.e. user click to sort.
	 */
	public String getSidx() {
		return sidx;
	}


	/**
	 * @param sidx get index row - i.e. user click to sort.
	 */
	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public void setSearchOper(String searchOper) {
		this.searchOper = searchOper;
	}

  @Override
  public void setSession(Map<String, Object> session)
  {
    this.session = session;    
  }

  public void setLoadonce(boolean loadonce)
  {
    this.loadonce = loadonce;
  }
}
