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
package fr.paris.lutece.plugins.workflow.modules.reassignment.service;

import fr.paris.lutece.plugins.workflow.modules.assignment.business.AssignmentHistory;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.assignment.service.IAssignmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.assignment.service.IWorkgroupConfigService;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ITaskCommentConfigService;
import fr.paris.lutece.plugins.workflow.modules.reassignment.business.TaskReassignmentConfig;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskReassignment
 *
 */
public class TaskReassignment extends Task
{
    // PARAMETERS
    private static final String PARAMETER_WORKGROUPS = "workgroups";

    // MARKS
    private static final String MARK_MESSAGE = "message";

    // TEMPLATE
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/notification/task_notification_mail.html";

    // MESSAGES
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.assignment.task_assignment_config.mailSenderName";

    // SERVICES
    @Inject
    private ITaskReassignmentConfigService _taskReassignementConfigService;
    @Inject
    private ITaskCommentConfigService _taskCommentConfigService;
    @Inject
    private IAssignmentHistoryService _assignmentHistoryService;
    @Inject
    private IWorkgroupConfigService _workgroupConfigService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(  )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS + "_" + this.getId(  ) );
        AdminUser admin = AdminUserService.getAdminUser( request );
        List<String> listWorkgroup = new ArrayList<String>(  );
        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( this.getId(  ),
                reassignPlugin, workflowPlugin );

        for ( int i = 0; i < tabWorkgroups.length; i++ )
        {
            listWorkgroup.add( tabWorkgroups[i] );

            //add history 
            AssignmentHistory history = new AssignmentHistory(  );
            history.setIdResourceHistory( nIdResourceHistory );
            history.setIdTask( this.getId(  ) );
            history.setWorkgroup( tabWorkgroups[i] );
            _assignmentHistoryService.create( history, workflowPlugin );

            if ( config.isNotify(  ) )
            {
                WorkgroupConfig workgroupConfig = _workgroupConfigService.findByPrimaryKey( this.getId(  ),
                        tabWorkgroups[i], workflowPlugin );

                if ( ( workgroupConfig != null ) &&
                        ( workgroupConfig.getIdMailingList(  ) != WorkflowUtils.CONSTANT_ID_NULL ) )
                {
                    Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( workgroupConfig.getIdMailingList(  ) );

                    String strSenderEmail = MailService.getNoReplyEmail(  );

                    HashMap<String, Object> model = new HashMap<String, Object>(  );
                    model.put( MARK_MESSAGE, config.getMessage(  ) );

                    HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model );

                    if ( config.isUseUserName(  ) )
                    {
                        String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );

                        // Send Mail
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail,
                                config.getSubject(  ), t.getHtml(  ) );
                        }
                    }
                    else
                    {
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail(  ),
                                admin.getFirstName(  ) + " " + admin.getLastName(  ), admin.getEmail(  ),
                                config.getSubject(  ), t.getHtml(  ) );
                        }
                    }
                }
            }
        }

        //update resource workflow 
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceHistory.getIdResource(  ),
                resourceHistory.getResourceType(  ), resourceHistory.getWorkflow(  ).getId(  ) );
        resourceWorkflow.setWorkgroups( listWorkgroup );
        _resourceWorkflowService.update( resourceWorkflow );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        //remove config
        _taskReassignementConfigService.remove( this.getId(  ), reassignPlugin, workflowPlugin );

        //remove task information
        _assignmentHistoryService.removeByTask( this.getId(  ), workflowPlugin );
        _workgroupConfigService.removeByTask( this.getId(  ), workflowPlugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        _assignmentHistoryService.removeByHistory( nIdHistory, this.getId(  ), workflowPlugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        Plugin reassignPlugin = PluginService.getPlugin( ReassignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        TaskReassignmentConfig config = _taskReassignementConfigService.findByPrimaryKey( this.getId(  ),
                reassignPlugin, workflowPlugin );

        if ( config != null )
        {
            return config.getTitle(  );
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        Map<String, String> mapListEntriesform = null;
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        TaskCommentConfig config = _taskCommentConfigService.findByPrimaryKey( this.getId(  ), workflowPlugin );

        if ( config != null )
        {
            mapListEntriesform = new HashMap<String, String>(  );
            mapListEntriesform.put( PARAMETER_WORKGROUPS + "_" + this.getId(  ), config.getTitle(  ) );
        }

        return mapListEntriesform;
    }
}
