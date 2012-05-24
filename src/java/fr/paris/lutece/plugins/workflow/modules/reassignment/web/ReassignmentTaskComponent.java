/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.reassignment.web;

import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.reassignment.business.TaskReassignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.reassignment.service.ITaskReassignmentConfigService;
import fr.paris.lutece.plugins.workflow.modules.reassignment.service.ReassignmentPlugin;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.TaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.mailinglist.MailingList;
import fr.paris.lutece.portal.business.mailinglist.MailingListHome;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroup;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * ReassignmentTaskComponent
 *
 */
public class ReassignmentTaskComponent extends TaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_REASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/reassignment/task_reassignment_config.html";
    private static final String TEMPLATE_TASK_REASSIGNMENT_FORM = "admin/plugins/workflow/modules/reassignment/task_reassignment_form.html";
    private static final String TEMPLATE_TASK_ASSIGNMENT_INFORMATION = "admin/plugins/workflow/modules/assignment/task_assignment_information.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WORKGROUP_LIST = "workgroup_list";
    private static final String MARK_ITEM = "item";
    private static final String MARK_MAILING_LIST = "mailing_list";

    // PARAMETERS
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_WORKGROUPS = "workgroups";
    private static final String PARAMETER_ID_MAILING_LIST = "id_mailing_list";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_IS_NOTIFICATION = "is_notify";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_IS_USE_USER_NAME = "is_use_user_name";

    // PROPERTIES
    private static final String FIELD_TITLE = "module.workflow.assignment.task_assignment_config.label_title";
    private static final String FIELD_MAILINGLIST_SUBJECT = "module.workflow.assignment.task_assignment_config.label_mailinglist_subject";
    private static final String FIELD_MAILINGLIST_MESSAGE = "module.workflow.assignment.task_assignment_config.label_mailinglist_message";
    private static final String PROPERTY_SELECT_EMPTY_CHOICE = "module.workflow.assignment.task_assignment_config.label_empty_choice";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.assignment.task_assignment_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_ASSIGNMENT = "module.workflow.assignment.task_assignment_config.message.no_configuration_for_task_comment";
    private static final String MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP = "module.workflow.assignment.task_assignment_config.message.no_mailinglist_for_workgroup";

    // XML TAGS
    private static final String TAG_ASSIGNMENT = "assignment";
    private static final String TAG_LIST_WORKGROUP = "list-workgroup";

    // SERVICES
    @Inject
    private ITaskReassignmentConfigService _taskReassignementConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strError = StringUtils.EMPTY;
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strIsNotification = request.getParameter( PARAMETER_IS_NOTIFICATION );
        String strIsUseUserName = request.getParameter( PARAMETER_IS_USE_USER_NAME );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS );

        if ( StringUtils.isBlank( strTitle ) )
        {
            strError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strIsNotification ) && StringUtils.isBlank( strSubject ) )
        {
            strError = FIELD_MAILINGLIST_SUBJECT;
        }

        if ( StringUtils.isNotBlank( strIsNotification ) && StringUtils.isBlank( strMessage ) )
        {
            strError = FIELD_MAILINGLIST_MESSAGE;
        }

        if ( StringUtils.isNotBlank( strError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( task.getId(  ),
                reassignPlugin, workflowPlugin );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskReassignmentConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
        }

        //add workgroups
        List<WorkgroupConfig> listWorkgroupConfig = new ArrayList<WorkgroupConfig>(  );
        WorkgroupConfig workgroupConfig;

        if ( tabWorkgroups != null )
        {
            for ( int i = 0; i < tabWorkgroups.length; i++ )
            {
                workgroupConfig = new WorkgroupConfig(  );
                workgroupConfig.setIdTask( task.getId(  ) );
                workgroupConfig.setWorkgroupKey( tabWorkgroups[i] );

                if ( strIsNotification != null )
                {
                    if ( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST + "_" +
                                    tabWorkgroups[i] ) ) != -1 )
                    {
                        workgroupConfig.setIdMailingList( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST +
                                    "_" + tabWorkgroups[i] ) ) );
                    }
                    else
                    {
                        return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP,
                            AdminMessage.TYPE_STOP );
                    }
                }

                listWorkgroupConfig.add( workgroupConfig );
            }
        }

        config.setTitle( strTitle );
        config.setNotify( strIsNotification != null );
        config.setWorkgroups( listWorkgroupConfig );
        config.setMessage( strMessage );
        config.setSubject( strSubject );
        config.setUseUserName( strIsUseUserName != null );

        if ( bCreate )
        {
            _taskReassignementConfigService.create( config, reassignPlugin, workflowPlugin );
        }
        else
        {
            _taskReassignementConfigService.update( config, reassignPlugin, workflowPlugin );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        String strError = StringUtils.EMPTY;
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS + "_" + task.getId(  ) );
        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( task.getId(  ),
                reassignPlugin, workflowPlugin );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_ASSIGNMENT,
                AdminMessage.TYPE_STOP );
        }

        if ( ( tabWorkgroups == null ) || ( tabWorkgroups.length == 0 ) )
        {
            strError = config.getTitle(  );
        }

        if ( StringUtils.isNotBlank( strError ) )
        {
            Object[] tabRequiredFields = { strError };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        String strNothing = I18nService.getLocalizedString( PROPERTY_SELECT_EMPTY_CHOICE, locale );

        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );

        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( task.getId(  ),
                reassignPlugin, workflowPlugin );

        List<Map<String, Object>> listWorkgroups = new ArrayList<Map<String, Object>>(  );

        for ( AdminWorkgroup workgroup : AdminWorkgroupHome.findAll(  ) )
        {
            Map<String, Object> workgroupsItem = new HashMap<String, Object>(  );
            workgroupsItem.put( MARK_ITEM, workgroup );

            if ( ( config != null ) && ( config.getWorkgroups(  ) != null ) )
            {
                for ( WorkgroupConfig workgroupSelected : config.getWorkgroups(  ) )
                {
                    if ( workgroup.getKey(  ).equals( workgroupSelected.getWorkgroupKey(  ) ) )
                    {
                        workgroupsItem.put( MARK_CONFIG, workgroupSelected );

                        break;
                    }
                }
            }

            listWorkgroups.add( workgroupsItem );
        }

        ReferenceList refMailingList = new ReferenceList(  );
        refMailingList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strNothing );

        ReferenceList refMailList = new ReferenceList(  );

        for ( MailingList mailingList : MailingListHome.findAll(  ) )
        {
            refMailList.addItem( mailingList.getId(  ), mailingList.getName(  ) );
        }

        refMailingList.addAll( refMailList );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKGROUP_LIST, listWorkgroups );
        model.put( MARK_CONFIG, config );
        model.put( MARK_MAILING_LIST, refMailingList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( task.getId(  ),
                reassignPlugin, workflowPlugin );

        model.put( MARK_CONFIG, config );
        model.put( MARK_WORKGROUP_LIST, AdminWorkgroupHome.findAll(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_REASSIGNMENT_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        ReferenceList refWorkgroups = new ReferenceList(  );

        model.put( MARK_WORKGROUP_LIST, refWorkgroups );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGNMENT_INFORMATION, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        StringBuffer strXml = new StringBuffer(  );

        XmlUtil.beginElement( strXml, TAG_ASSIGNMENT );
        XmlUtil.beginElement( strXml, TAG_LIST_WORKGROUP );

        XmlUtil.endElement( strXml, TAG_LIST_WORKGROUP );
        XmlUtil.endElement( strXml, TAG_ASSIGNMENT );

        return strXml.toString(  );
    }
}
