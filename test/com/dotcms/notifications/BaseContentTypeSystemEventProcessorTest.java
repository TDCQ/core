package com.dotcms.notifications;


import com.dotcms.api.system.event.Payload;
import com.dotcms.api.system.event.SystemEvent;
import com.dotcms.api.system.event.SystemEventType;
import com.dotcms.api.system.event.Visibility;
import com.dotcms.api.web.HttpServletRequestThreadLocal;
import com.dotcms.repackage.com.bradmcevoy.http.Response;
import com.dotcms.rest.api.v1.content.ContentTypeView;
import com.dotcms.rest.api.v1.system.websocket.SessionWrapper;
import com.dotmarketing.portlets.structure.business.StructureAPI;
import com.dotmarketing.portlets.structure.model.Structure;
import com.liferay.portal.model.User;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseContentTypeSystemEventProcessorTest {

    @Test
    public void testProcess(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        SystemEvent event = mock(SystemEvent.class);
        Structure structure = mock(Structure.class);
        Payload payload = mock(Payload.class);
        SessionWrapper session = mock(SessionWrapper.class);
        User user = new User();
        StructureAPI structureAPI = mock(StructureAPI.class);
        SystemEventType systemEventType = SystemEventType.SAVE_BASE_CONTENT_TYPE;

        HttpServletRequestThreadLocal httpServletRequestThreadLocal = mock(HttpServletRequestThreadLocal.class);

        when(httpServletRequestThreadLocal.getRequest()).thenReturn(request);
        when(session.getUser()).thenReturn(user);
        when(structureAPI.getActionUrl(request, structure, user)).thenReturn("http://localhost:8080");
        when(event.getId()).thenReturn("1");
        when(event.getEventType()).thenReturn(systemEventType);
        when(event.getPayload()).thenReturn(payload);
        when(payload.getData()).thenReturn(structure);
        when(payload.getVisibilityId()).thenReturn("1");
        when(structure.getStructureType()).thenReturn(Structure.Type.CONTENT.getType());
        when(structure.getName()).thenReturn("test structure");
        when(structure.getInode()).thenReturn("3b276d59-46e3-4196-9169-639ddfe6677f");

        BaseContentTypeSystemEventProcessor baseContentTypeSystemEventProcessor =
                new BaseContentTypeSystemEventProcessor(structureAPI, httpServletRequestThreadLocal);
        SystemEvent result = baseContentTypeSystemEventProcessor.process(event, session);

        assertEquals(result.getEventType(), systemEventType);
        assertEquals(result.getId(), "1");
        ContentTypeView contentTypeView = ContentTypeView.class.cast(result.getPayload().getData());
        assertEquals(Structure.Type.CONTENT.toString(), contentTypeView.getType());
        assertEquals( "test structure", contentTypeView.getName());
        assertEquals("3b276d59-46e3-4196-9169-639ddfe6677f", contentTypeView.getInode());
        assertEquals("http://localhost:8080", contentTypeView.getAction());
    }
}
