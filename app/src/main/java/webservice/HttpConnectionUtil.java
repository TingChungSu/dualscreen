package webservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class HttpConnectionUtil 
{    
	static String SERVICE_URL = "http://www.aaa-info.club:8081/ZenYunWebService.asmx";
	static String NAMESPACE   = "http://tempuri.org/";
	public static int threadCnt = 0;
	
 	static private SoapObject sendRequest(String method, Map<String, String> mapProperty)
 	{
 		SoapObject result = null;
 		HttpTransportSE androidHttpTransport = null;
    	try {
	 		SoapObject so = new SoapObject(NAMESPACE, method);
	 		
	 		Iterator<?> it = mapProperty.entrySet().iterator();  
			while (it.hasNext()) {  
				 Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();  
				 Object key = entry.getKey();  
				 Object value = entry.getValue();  
				 so.addProperty(key.toString(), value.toString());
			}  
	 		
	 		String action = NAMESPACE + method;
	 		
	    	//Declare the version of the SOAP request
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	
	    	envelope.setOutputSoapObject(so);
	    	envelope.dotNet = true;
	    	
    		androidHttpTransport = new HttpTransportSE(SERVICE_URL, 10000);
    		androidHttpTransport.call(action, envelope);
    		
        	result = (SoapObject)envelope.bodyIn;
    	} catch (ClientProtocolException e) {    
            Log.e("HttpConnectionUtil", e.getMessage(), e);    
        } catch (Exception e) {    
            Log.e("HttpConnectionUtil", e.getMessage(), e);
        } finally {       
            if (androidHttpTransport != null && androidHttpTransport.getConnection() != null) {
            	try {
					androidHttpTransport.getConnection().disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
                androidHttpTransport = null;
            }
        }  
    	return result;
 	}
    
    
 	static public void asyncSendRequestT(final String method, final Map<String, String> mapProperty, final Handler callback) 
    {    
        Thread thread=new Thread(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
            	threadCnt++;
            	try
            	{
            		syncSendRequestT(method, mapProperty, callback);
	            } catch (Exception e) {    
	                Log.e("HttpConnectionUtil", e.getMessage(), e);
	                threadCnt--;
	            }
            	
            }  
        });  
        thread.start(); 
    }   
    
 	static private void syncSendRequestT(final String method, final Map<String, String> mapProperty, final Handler callback) 
 	{    

        SoapObject ret = sendRequest(method, mapProperty);
        
        if (callback!=null) {
        	Message msg = new Message();
        	Bundle data = new Bundle();
        	data.putString("method", method);
        	if(ret!=null) {
        		data.putString("response", ret.getProperty(0).toString());
        	}
        	else {
        		data.putString("response", null);
        	}
        	
        	threadCnt--;
        	
        	if(threadCnt == 0)
        		data.putString("request.done", "true");
        	else
        		data.putString("request.done", "false");
        	
        	msg.setData(data);
        	callback.sendMessage(msg);
        }
    }    
    
    
    
    public static final String HEX_DIGITS = "0123456789ABCDEF";
    
    static private char decodeUTF8(String src) {
        if (src == null) {
          throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
        }

        if (!(src.startsWith("\\u") && src.length() <= 6)) {
          throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
        }

        char[] sources = src.substring(2).toCharArray();
        char res = 0;
        for (char nextChar : sources) {
          int digit = HEX_DIGITS.indexOf(Character.toUpperCase(nextChar));
          res = (char) (res * 16 + digit);
        }
        return res;
      }

      public String decodeUTF8String(String src) {
        StringBuilder sb = new StringBuilder();
        char[] sources = src.toCharArray();
        for (int i = 0; i < sources.length; i++) {
          if (sources[i] == '\\' && i < sources.length - 5
              && sources[i + 1] == 'u') 
          {
            String utf8 = "" + sources[i++] + sources[i++] + sources[i++]
                + sources[i++] + sources[i++] + sources[i];
            sb.append(decodeUTF8(utf8));
           // i = i + 5;
          } else {
            sb.append(sources[i]);
          }
        }
        return sb.toString();
      }
      
      
          	  
    	 public static String decodeUnicode(String theString) {  
	    char aChar;  
	    int len = theString.length();  
	    StringBuffer outBuffer = new StringBuffer(len);  
	    for (int x = 0; x < len;) {  
	        aChar = theString.charAt(x++);  
	        if (aChar == '\\') {  
	            aChar = theString.charAt(x++);  
	            if (aChar == 'u') {  
	                // Read the xxxx  
	                int value = 0;  
	                for (int i = 0; i < 4; i++) {  
	                    aChar = theString.charAt(x++);  
	                    switch (aChar) {  
	                    case '0':  
	                    case '1':  
	                    case '2':  
	                    case '3':  
	                    case '4':  
	                    case '5':  
	                    case '6':  
	                    case '7':  
	                    case '8':  
	                    case '9':  
	                        value = (value << 4) + aChar - '0';  
	                        break;  
	                    case 'a':  
	                    case 'b':  
	                    case 'c':  
	                    case 'd':  
	                    case 'e':  
	                    case 'f':  
	                        value = (value << 4) + 10 + aChar - 'a';  
	                        break;  
	                    case 'A':  
	                    case 'B':  
	                    case 'C':  
	                    case 'D':  
	                    case 'E':  
	                    case 'F':  
	                        value = (value << 4) + 10 + aChar - 'A';  
	                        break;  
	                    default:  
	                        throw new IllegalArgumentException(  
	                                "Malformed   \\uxxxx   encoding.");  
	                    }  
	  
	                }  
	                outBuffer.append((char) value);  
	            } else {  
	                if (aChar == 't')  
	                    aChar = '\t';  
	                else if (aChar == 'r')  
	                    aChar = '\r';  
	                else if (aChar == 'n')  
	                    aChar = '\n';  
	                else if (aChar == 'f')  
	                    aChar = '\f';  
	                outBuffer.append(aChar);  
	            }  
	        } else  
	            outBuffer.append(aChar);  
	    }  
	    return outBuffer.toString();  
	}      	  
    	  
}   