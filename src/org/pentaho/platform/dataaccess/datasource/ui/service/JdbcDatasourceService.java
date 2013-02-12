package org.pentaho.platform.dataaccess.datasource.ui.service;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.platform.dataaccess.datasource.DatasourceInfo;
import org.pentaho.platform.dataaccess.datasource.IDatasourceInfo;
import org.pentaho.platform.dataaccess.datasource.wizard.controllers.ConnectionController;
import org.pentaho.platform.dataaccess.datasource.wizard.controllers.MessageHandler;
import org.pentaho.ui.database.event.IConnectionAutoBeanFactory;
import org.pentaho.ui.database.event.IDatabaseConnectionList;
//import org.pentaho.platform.dataaccess.datasource.wizard.service.IXulAsyncConnectionService;
import org.pentaho.ui.xul.XulServiceCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class JdbcDatasourceService implements IUIDatasourceAdminService{
  
  public static final String TYPE = "JDBC";
  private boolean editable = false;
  private boolean removable = true;
  private boolean importable = true;
  private boolean exportable = true;
  private String newUI = "builtin:";
  private String editUI = "builtin:";

  protected IConnectionAutoBeanFactory connectionAutoBeanFactory;  

  public JdbcDatasourceService(/*IXulAsyncConnectionService connectionService*/) {
    connectionAutoBeanFactory = GWT.create(IConnectionAutoBeanFactory.class);
  }
  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void getIds(final XulServiceCallback<List<IDatasourceInfo>> callback) {
    RequestBuilder listConnectionBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("http://localhost:8080/pentaho/plugin/data-access/api/connection/list"));
    listConnectionBuilder.setHeader("Content-Type", "application/json");
    try {
      listConnectionBuilder.sendRequest(null, new RequestCallback() {
        
        @Override
        public void onError(Request request, Throwable exception) {
          callback.error(exception.getMessage(), exception);
        }
       
        @Override
        public void onResponseReceived(Request request, Response response) {
          AutoBean<IDatabaseConnectionList> bean = AutoBeanCodex.decode(connectionAutoBeanFactory, IDatabaseConnectionList.class, response.getText());
          List<IDatabaseConnection> connections = bean.as().getDatabaseConnections();
          List<IDatasourceInfo> datasourceInfos = new ArrayList<IDatasourceInfo>();
          for(IDatabaseConnection connection:connections) {
            datasourceInfos.add(new DatasourceInfo(connection.getName(), connection.getName(), TYPE, editable, removable, importable, exportable));
          }
          callback.success(datasourceInfos);
        }
     });
    } catch (RequestException e) {
      callback.error(e.getMessage(), e);
    }
  }

  @Override
  public String getNewUI() {
    return newUI;
  }

  /* (non-Javadoc)
   * @see org.pentaho.platform.dataaccess.datasource.ui.service.IUIDatasourceAdminService#getEditUI(org.pentaho.platform.dataaccess.datasource.IDatasourceInfo)
   */
  @Override
  public String getEditUI(IDatasourceInfo dsInfo) {
    return editUI;
  }

  
  /* (non-Javadoc)
   * @see org.pentaho.platform.dataaccess.datasource.ui.service.IUIDatasourceAdminService#export(org.pentaho.platform.dataaccess.datasource.IDatasourceInfo)
   */
  @Override
  public void export(IDatasourceInfo dsInfo) {
    // TODO Auto-generated method stub
    
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.platform.dataaccess.datasource.ui.service.IUIDatasourceAdminService#remove(org.pentaho.platform.dataaccess.datasource.IDatasourceInfo)
   */
  @Override
  public void remove(IDatasourceInfo dsInfo, Object callback) {
    RequestBuilder deleteConnectionBuilder = new RequestBuilder(RequestBuilder.DELETE, URL.encode("http://localhost:8080/pentaho/plugin/data-access/api/connection/deletebyname?name=" + dsInfo.getName())); 
    try {
      deleteConnectionBuilder.sendRequest(null, (RequestCallback) callback);
    } catch (RequestException e) {
    }
  }

//  /* (non-Javadoc)
//   * @see org.pentaho.platform.dataaccess.datasource.ui.service.IUIDatasourceAdminService#remove(org.pentaho.platform.dataaccess.datasource.IDatasourceInfo)
//   */
//  @Override
//  public void remove(IDatasourceInfo dsInfo, XulServiceCallback<Boolean> callback) {
//    connectionService.deleteConnection(dsInfo.getName(), callback);
//  }

}