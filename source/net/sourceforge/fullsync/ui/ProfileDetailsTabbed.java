package net.sourceforge.fullsync.ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import net.full.fs.ui.ConnectionConfiguration;
import net.full.fs.ui.LocationDescription;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.schedule.Schedule;

import org.apache.commons.vfs.FileSystemException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
public class ProfileDetailsTabbed extends org.eclipse.swt.widgets.Composite {
	
	private static final String EXPANDED_KEY = "Expanded";
	private static final String FILTER_KEY = "Filter";
	
	private ProfileManager profileManager;
	private Label label18;
	private Button buttonFileFilter;
	private Button buttonResetError;
	private Button buttonEnabled;
	private Button buttonScheduling;
	private Label labelTypeDescription;
	private Combo comboType;
	private Label label16;
	private Text textDescription;
	private Label label15;
	private Label label1;
	private Text textRuleSet;
	private Label label4;
	private Group advancedRuleOptionsGroup;
	private Group groupDestination;
	private ConnectionConfiguration connectionConfigurationDestination;
	private ConnectionConfiguration connectionConfigurationSource;
	private TreeItem treeItemSubDirs;
	private TreeItem treeItemFilters;
	private TreeItem treeItemLocations;
	private TreeItem treeItemGeneral;
	private Group groupAutomated;
	private Group groupGeneral;
	private Group groupSource;
	private Composite compositeTabSubdirs;
	private Composite compositeTabFilters;
	private Composite compositeTabLocations;
	private Composite compositeTabGeneral;
	private Composite compositeMain;
	private Tree treeTabs;
	private SashForm sashForm;
	private Button buttonUseFileFilter;
	private Label labelFilterDescription;
	private Button syncSubsButton;
	private Group simplyfiedOptionsGroup;
	private Button rbAdvancedRuleSet;
	private Button rbSimplyfiedRuleSet;
	private Group ruleSetGroup;
	private Label label12;
	private Label label11;
	private Label label10;
	private Button buttonDestinationBuffered;
	private Label label8;
	private Label label6;
	private Label label5;
	private Button buttonSourceBuffered;
	private Text textName;
	
	private Tree directoryTree;
	private Vector treeItemsWithFilter = new Vector();
	private HashMap itemsMap;
	private Site sourceSite;
	private FileSystemManager fsm = new FileSystemManager();
	
	private Color selectedColor = new Color(null, 0, 0, 255);
	private Color unselectedColor = new Color(null, 200, 200, 200);
	private Color othersColor = new Color(null, 0, 0, 0);

	private String profileName;
	
	private FileFilter filter;
		
	public ProfileDetailsTabbed(Composite parent, int style) {
		super(parent, style);
		initGUI();
		this.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent arg0) {
				closeSourceSite();			}	
		});
	}
	
	/**
	 * Initializes the GUI.
	 * Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI(){
		try {
			preInitGUI();
			
			GridLayout thisLayout = new GridLayout();
			thisLayout.marginWidth = 0;
			thisLayout.marginHeight = 0;
			this.setLayout(thisLayout);
			{
				sashForm = new SashForm(this, SWT.NONE);
				GridData sashFormLData = new GridData();
				sashFormLData.grabExcessHorizontalSpace = true;
				sashFormLData.grabExcessVerticalSpace = true;
				sashFormLData.horizontalAlignment = GridData.FILL;
				sashFormLData.verticalAlignment = GridData.FILL;
				sashForm.setLayoutData(sashFormLData);
				sashForm.setSize(60, 30);
				{
					treeTabs = new Tree(sashForm, SWT.NONE);
					treeTabs.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							treeTabsWidgetSelected(evt);
						}
					});
					{
						treeItemGeneral = new TreeItem(treeTabs, SWT.NONE);
						treeItemGeneral.setText("General");
					}
					{
						treeItemLocations = new TreeItem(treeTabs, SWT.NONE);
						treeItemLocations.setText("Locations");
					}
					{
						treeItemFilters = new TreeItem(treeTabs, SWT.NONE);
						treeItemFilters.setText("Filters");
					}
					{
						treeItemSubDirs = new TreeItem(treeTabs, SWT.NONE);
						treeItemSubDirs.setText("Subdirectories");
					}
				}
                {
                    compositeMain = new Composite( sashForm, SWT.NONE);
                    StackLayout compositeMainLayout2 = new StackLayout();
                    compositeMain.setLayout(compositeMainLayout2);
                    {
                        compositeTabGeneral = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemGeneral.setData(compositeTabGeneral);
                        GridLayout compositeTabGeneralLayout = new GridLayout();
                        compositeTabGeneral
                            .setLayout(compositeTabGeneralLayout);
                        {
                            groupGeneral = new Group(
                                compositeTabGeneral,
                                SWT.NONE);
                            GridLayout groupGeneralLayout = new GridLayout();
                            groupGeneralLayout.numColumns = 2;
                            groupGeneral.setLayout(groupGeneralLayout);
                            GridData groupGeneralLData = new GridData();
                            groupGeneralLData.grabExcessHorizontalSpace = true;
                            groupGeneralLData.horizontalAlignment = GridData.FILL;
                            groupGeneral.setLayoutData(groupGeneralLData);
                            groupGeneral.setText("General");
                            {
                                label1 = new Label(groupGeneral, SWT.NONE);
                                label1
                                    .setText(Messages
                                        .getString("ProfileDetails.Name.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                                GridData textNameLData = new GridData();
                                textNameLData.grabExcessHorizontalSpace = true;
                                textNameLData.horizontalAlignment = GridData.FILL;
                                textName = new Text(
                                    groupGeneral,
                                    SWT.BORDER);
                                textName.setLayoutData(textNameLData);
                                textName
                                    .setToolTipText(Messages
                                        .getString("ProfileDetails.Name.ToolTip")); //$NON-NLS-1$
                            }
                            {
                                label15 = new Label(groupGeneral, SWT.NONE);
                                label15
                                    .setText(Messages
                                        .getString("ProfileDetails.Description.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                                GridData textDescriptionLData = new GridData();
                                textDescriptionLData.horizontalAlignment = GridData.FILL;
                                textDescription = new Text(
                                    groupGeneral,
                                    SWT.BORDER);
                                textDescription
                                    .setLayoutData(textDescriptionLData);
                            }
                            {
                                label16 = new Label(groupGeneral, SWT.NONE);
                                label16
                                    .setText(Messages
                                        .getString("ProfileDetails.Type.Label")); //$NON-NLS-1$
                            }
                            {
                                comboType = new Combo(
                                    groupGeneral,
                                    SWT.DROP_DOWN | SWT.READ_ONLY);
                                comboType
                                    .addModifyListener(new ModifyListener() {
                                        public void modifyText(
                                            ModifyEvent evt) {
                                            if (comboType.getText().equals(
                                                "Publish/Update")) //$NON-NLS-1$
                                            {
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.Publish")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(true);
                                            } else if (comboType.getText()
                                                .equals("Backup Copy")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.BackupCopy")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            } else if (comboType.getText()
                                                .equals("Exact Copy")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.ExactCopy")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            } else if (comboType.getText()
                                                .equals("Two Way Sync")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.TwoWaySync")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            }
                                        }
                                    });
                            }
                            {
                                GridData labelTypeDescriptionLData = new GridData();
                                labelTypeDescriptionLData.horizontalSpan = 2;
                                labelTypeDescriptionLData.horizontalAlignment = GridData.FILL;
                                labelTypeDescription = new Label(
                                    groupGeneral,
                                    SWT.WRAP);
                                labelTypeDescription
                                    .setLayoutData(labelTypeDescriptionLData);
                                labelTypeDescription
                                    .setText(Messages
                                        .getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
                            }
                        }
                        {
                            groupAutomated = new Group(
                                compositeTabGeneral,
                                SWT.NONE);
                            GridLayout groupAutomatedLayout = new GridLayout();
                            groupAutomatedLayout.makeColumnsEqualWidth = true;
                            groupAutomatedLayout.numColumns = 2;
                            groupAutomated.setLayout(groupAutomatedLayout);
                            GridData groupAutomatedLData = new GridData();
                            groupAutomatedLData.horizontalAlignment = GridData.FILL;
                            groupAutomated
                                .setLayoutData(groupAutomatedLData);
                            groupAutomated.setText("Automated execution");
                            {
                                buttonScheduling = new Button(
                                    groupAutomated,
                                    SWT.PUSH | SWT.CENTER);
                                buttonScheduling
                                    .setText(Messages
                                        .getString("ProfileDetails.Edit_Scheduling")); //$NON-NLS-1$
                                buttonScheduling
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            ScheduleSelectionDialog dialog = new ScheduleSelectionDialog(
                                                getShell(),
                                                SWT.NULL);
                                            dialog
                                                .setSchedule((Schedule) buttonScheduling
                                                    .getData());
                                            dialog.open();

                                            buttonScheduling.setData(dialog
                                                .getSchedule());
                                        }
                                    });
                            }
                            {
                                GridData buttonResetErrorLData = new GridData();
                                buttonResetErrorLData.horizontalSpan = 2;
                                buttonResetError = new Button(
                                    groupAutomated,
                                    SWT.CHECK | SWT.RIGHT);
                                buttonResetError
                                    .setLayoutData(buttonResetErrorLData);
                                buttonResetError
                                    .setText(Messages
                                        .getString("ProfileDetails.Reset_ErrorFlag")); //$NON-NLS-1$
                            }
                            {
                                GridData buttonEnabledLData = new GridData();
                                buttonEnabledLData.horizontalSpan = 2;
                                buttonEnabled = new Button(
                                    groupAutomated,
                                    SWT.CHECK | SWT.RIGHT);
                                buttonEnabled
                                    .setLayoutData(buttonEnabledLData);
                                buttonEnabled.setText(Messages
                                    .getString("ProfileDetails.Enabled")); //$NON-NLS-1$
                            }
                        }
                    }
                    {
                        compositeTabLocations = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemLocations.setData(compositeTabLocations);
                        GridLayout compositeTabLocationsLayout = new GridLayout();
                        GridData compositeTabLocationsLData = new GridData();
                        compositeTabLocations
                            .setLayoutData(compositeTabLocationsLData);
                        compositeTabLocations
                            .setLayout(compositeTabLocationsLayout);
                        {
                            groupSource = new Group(
                                compositeTabLocations,
                                SWT.NONE);
                            GridLayout groupSourceLayout = new GridLayout();
                            groupSource.setLayout(groupSourceLayout);
                            GridData groupSourceLData = new GridData();
                            groupSourceLData.grabExcessHorizontalSpace = true;
                            groupSourceLData.horizontalAlignment = GridData.FILL;
                            groupSourceLData.grabExcessVerticalSpace = true;
                            groupSourceLData.verticalAlignment = GridData.FILL;
                            groupSource.setLayoutData(groupSourceLData);
                            groupSource.setText(Messages
                                .getString("ProfileDetails.Source.Label")); //$NON-NLS-1$ 
                            {
                                GridData connectionConfigurationSourceLData = new GridData();
                                connectionConfigurationSourceLData.horizontalAlignment = GridData.FILL;
                                connectionConfigurationSourceLData.verticalAlignment = GridData.FILL;
                                connectionConfigurationSourceLData.grabExcessHorizontalSpace = true;
                                connectionConfigurationSource = new ConnectionConfiguration(
                                    groupSource,
                                    SWT.NONE);
                                connectionConfigurationSource
                                    .setLayoutData(connectionConfigurationSourceLData);
                                /*
                                connectionConfigurationSource
                                    .addControlListener(new ControlAdapter() {
                                    public void controlResized(
                                        ControlEvent evt) {
                                        if( ((StackLayout)compositeMain.getLayout()).topControl == compositeTabLocations )
                                        {
                                            compositeMain.pack();
                                            scrolledComposite.layout();
                                        }
                                    }
                                    });
                                    */
                            }
                            {
                                buttonSourceBuffered = new Button(
                                    groupSource,
                                    SWT.CHECK | SWT.LEFT);
                                buttonSourceBuffered
                                    .setText(Messages
                                        .getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                                GridData buttonSourceBufferedLData = new GridData();
                                buttonSourceBufferedLData.horizontalSpan = 3;
                                buttonSourceBuffered
                                    .setLayoutData(buttonSourceBufferedLData);
                                buttonSourceBuffered.setEnabled(false);
                            }

                        }
                        {
                            groupDestination = new Group(
                                compositeTabLocations,
                                SWT.NONE);
                            GridLayout groupDestinationLayout = new GridLayout();
                            groupDestination
                                .setLayout(groupDestinationLayout);
                            GridData groupDestinationLData = new GridData();
                            groupDestinationLData.horizontalAlignment = GridData.FILL;
                            groupDestinationLData.grabExcessHorizontalSpace = true;
                            groupDestinationLData.grabExcessVerticalSpace = true;
                            groupDestinationLData.verticalAlignment = GridData.FILL;
                            groupDestination
                                .setLayoutData(groupDestinationLData);
                            groupDestination
                                .setText(Messages
                                    .getString("ProfileDetails.Destination.Label")); //$NON-NLS-1$
                            {
                                connectionConfigurationDestination = new ConnectionConfiguration(
                                    groupDestination,
                                    SWT.NONE);
                                GridData connectionConfiguration1LData = new GridData();
                                connectionConfiguration1LData.verticalAlignment = GridData.FILL;
                                connectionConfiguration1LData.horizontalAlignment = GridData.FILL;
                                connectionConfiguration1LData.grabExcessHorizontalSpace = true;
                                connectionConfigurationDestination
                                    .setLayoutData(connectionConfiguration1LData);
                                /*
                                connectionConfigurationDestination
                                    .addControlListener(new ControlAdapter() {
                                    public void controlResized(
                                        ControlEvent evt) {
                                        if( ((StackLayout)compositeMain.getLayout()).topControl == compositeTabLocations )
                                        {
                                            compositeMain.pack();
                                            scrolledComposite.layout();
                                        }
                                    }
                                    });*/
                            }
                            {
                                GridData buttonDestinationBufferedLData = new GridData();
                                buttonDestinationBufferedLData.horizontalSpan = 3;
                                buttonDestinationBuffered = new Button(
                                    groupDestination,
                                    SWT.CHECK | SWT.LEFT);
                                buttonDestinationBuffered
                                    .setLayoutData(buttonDestinationBufferedLData);
                                buttonDestinationBuffered
                                    .setText(Messages
                                        .getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                                //buttonDestinationBuffered.setEnabled( false );
                            }
                        }
                    }
                    {
                        compositeTabFilters = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemFilters.setData(compositeTabFilters);
                        GridLayout compositeTabFiltersLayout = new GridLayout();
                        compositeTabFiltersLayout.makeColumnsEqualWidth = true;
                        compositeTabFilters
                            .setLayout(compositeTabFiltersLayout);
                        {
                            ruleSetGroup = new Group(
                                compositeTabFilters,
                                SWT.NONE);
                            GridLayout ruleSetGroupLayout = new GridLayout();
                            ruleSetGroupLayout.makeColumnsEqualWidth = true;
                            ruleSetGroupLayout.horizontalSpacing = 20;
                            GridData ruleSetGroupLData = new GridData();
                            ruleSetGroupLData.horizontalAlignment = GridData.FILL;
                            ruleSetGroupLData.grabExcessHorizontalSpace = true;
                            ruleSetGroup.setLayoutData(ruleSetGroupLData);
                            ruleSetGroup.setLayout(ruleSetGroupLayout);
                            ruleSetGroup.setText(Messages
                                .getString("ProfileDetails.RuleSet")); //$NON-NLS-1$
                            {
                                rbSimplyfiedRuleSet = new Button(
                                    ruleSetGroup,
                                    SWT.RADIO | SWT.LEFT);
                                rbSimplyfiedRuleSet
                                    .setText(Messages
                                        .getString("ProfileDetails.Simple_Rule_Set")); //$NON-NLS-1$
                                rbSimplyfiedRuleSet.setSelection(true);
                                GridData rbSimplyfiedRuleSetLData = new GridData();
                                rbSimplyfiedRuleSetLData.grabExcessHorizontalSpace = true;
                                rbSimplyfiedRuleSetLData.horizontalAlignment = GridData.FILL;
                                rbSimplyfiedRuleSet
                                    .setLayoutData(rbSimplyfiedRuleSetLData);
                                rbSimplyfiedRuleSet
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            selectRuleSetButton(rbSimplyfiedRuleSet);
                                        }
                                    });
                            }
                            {
                                simplyfiedOptionsGroup = new Group(
                                    ruleSetGroup,
                                    SWT.NONE);
                                GridLayout simplyfiedOptionsGroupLayout = new GridLayout();
                                GridData simplyfiedOptionsGroupLData = new GridData();
                                simplyfiedOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                                simplyfiedOptionsGroupLData.grabExcessHorizontalSpace = true;
                                simplyfiedOptionsGroupLData.horizontalAlignment = GridData.FILL;
                                simplyfiedOptionsGroup
                                    .setLayoutData(simplyfiedOptionsGroupLData);
                                simplyfiedOptionsGroupLayout.numColumns = 3;
                                simplyfiedOptionsGroup
                                    .setLayout(simplyfiedOptionsGroupLayout);
                                simplyfiedOptionsGroup
                                    .setText(Messages
                                        .getString("ProfileDetails.Simple_Rule_Options")); //$NON-NLS-1$
                                {
                                    syncSubsButton = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.CHECK | SWT.LEFT);
                                    syncSubsButton
                                        .setText(Messages
                                            .getString("ProfileDetails.Sync_SubDirs")); //$NON-NLS-1$
                                    GridData syncSubsButtonLData = new GridData();
                                    syncSubsButton
                                        .setToolTipText(Messages
                                            .getString("ProfileDetails.Rucurre")); //$NON-NLS-1$
                                    syncSubsButtonLData.horizontalSpan = 3;
                                    syncSubsButton
                                        .setLayoutData(syncSubsButtonLData);
                                }
                                {
                                    buttonUseFileFilter = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.CHECK | SWT.LEFT);
                                    GridData buttonUseFileFilterLData = new GridData();
                                    buttonUseFileFilterLData.horizontalSpan = 3;
                                    buttonUseFileFilter
                                        .setLayoutData(buttonUseFileFilterLData);
                                    buttonUseFileFilter
                                        .setText("Use file filter");
                                    buttonUseFileFilter.setSelection(true);
                                    buttonUseFileFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(
                                                SelectionEvent evt) {
                                                if (buttonUseFileFilter
                                                    .getSelection()) {
                                                    label18
                                                        .setEnabled(true);
                                                    buttonFileFilter
                                                        .setEnabled(true);
                                                    labelFilterDescription
                                                        .setEnabled(true);
                                                } else {
                                                    label18
                                                        .setEnabled(false);
                                                    buttonFileFilter
                                                        .setEnabled(false);
                                                    labelFilterDescription
                                                        .setEnabled(false);
                                                }
                                            }
                                        });
                                }
                                {
                                    label18 = new Label(
                                        simplyfiedOptionsGroup,
                                        SWT.NONE);
                                    label18.setText("Files Filter: ");
                                }
                                {
                                    buttonFileFilter = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.PUSH | SWT.CENTER);
                                    buttonFileFilter
                                        .setText("Set Filter...");
                                    GridData buttonFileFilterLData = new GridData();
                                    buttonFileFilterLData.horizontalSpan = 2;
                                    buttonFileFilter
                                        .setLayoutData(buttonFileFilterLData);
                                    buttonFileFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(
                                                SelectionEvent evt) {
                                                try {
                                                    WizardDialog dialog = new WizardDialog(
                                                        getShell(),
                                                        SWT.APPLICATION_MODAL);
                                                    FileFilterPage page = new FileFilterPage(
                                                        dialog,
                                                        filter);
                                                    dialog.show();
                                                    FileFilter newfilter = page
                                                        .getFileFilter();
                                                    if (newfilter != null) {
                                                        filter = newfilter;
                                                        labelFilterDescription
                                                            .setText(filter
                                                                .toString());
                                                    }
                                                } catch (Exception e) {
                                                    ExceptionHandler
                                                        .reportException(e);
                                                }
                                            }
                                        });
                                }
                                {
                                    labelFilterDescription = new Label(
                                        simplyfiedOptionsGroup,
                                        SWT.SHADOW_NONE
                                            | SWT.WRAP
                                            | SWT.BORDER);
                                    GridData labelFilterDescriptionLData = new GridData();
                                    labelFilterDescriptionLData.horizontalSpan = 3;
                                    labelFilterDescriptionLData.horizontalAlignment = GridData.FILL;
                                    labelFilterDescriptionLData.heightHint = 48;
                                    labelFilterDescriptionLData.widthHint = 300;
                                    labelFilterDescriptionLData.grabExcessHorizontalSpace = true;
                                    labelFilterDescription
                                        .setLayoutData(labelFilterDescriptionLData);
                                    labelFilterDescription.setText("");
                                }
                            }
                            {
                                rbAdvancedRuleSet = new Button(
                                    ruleSetGroup,
                                    SWT.RADIO | SWT.LEFT);
                                rbAdvancedRuleSet
                                    .setText(Messages
                                        .getString("ProfileDetails.Advanced_Rule_Set")); //$NON-NLS-1$
                                GridData rbAdvancedRuleSetLData = new GridData();
                                rbAdvancedRuleSetLData.grabExcessHorizontalSpace = true;
                                rbAdvancedRuleSetLData.horizontalAlignment = GridData.FILL;
                                rbAdvancedRuleSet
                                    .setLayoutData(rbAdvancedRuleSetLData);
                                rbAdvancedRuleSet
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            selectRuleSetButton(rbAdvancedRuleSet);
                                        }
                                    });
                            }
                            {
                                advancedRuleOptionsGroup = new Group(
                                    ruleSetGroup,
                                    SWT.NONE);
                                GridLayout advancedRuleOptionsGroupLayout = new GridLayout();
                                GridData advancedRuleOptionsGroupLData = new GridData();
                                advancedRuleOptionsGroup.setEnabled(false);
                                advancedRuleOptionsGroupLData.heightHint = 31;
                                advancedRuleOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                                advancedRuleOptionsGroupLData.grabExcessHorizontalSpace = true;
                                advancedRuleOptionsGroupLData.horizontalAlignment = GridData.FILL;
                                advancedRuleOptionsGroup
                                    .setLayoutData(advancedRuleOptionsGroupLData);
                                advancedRuleOptionsGroupLayout.numColumns = 2;
                                advancedRuleOptionsGroup
                                    .setLayout(advancedRuleOptionsGroupLayout);
                                advancedRuleOptionsGroup
                                    .setText(Messages
                                        .getString("ProfileDetails.Advanced_Rule_Options")); //$NON-NLS-1$
                                {
                                    label4 = new Label(
                                        advancedRuleOptionsGroup,
                                        SWT.NONE);
                                    GridData label4LData = new GridData();
                                    label4.setEnabled(false);
                                    label4.setLayoutData(label4LData);
                                    label4
                                        .setText(Messages
                                            .getString("ProfileDetails.RuleSet_2")); //$NON-NLS-1$
                                }
                                {
                                    textRuleSet = new Text(
                                        advancedRuleOptionsGroup,
                                        SWT.BORDER);
                                    GridData textRuleSetLData = new GridData();
                                    textRuleSet.setEnabled(false);
                                    textRuleSetLData.widthHint = 100;
                                    textRuleSetLData.heightHint = 13;
                                    textRuleSet
                                        .setLayoutData(textRuleSetLData);
                                }
                            }
                        }
                    }
                    {
                        compositeTabSubdirs = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemSubDirs.setData(compositeTabSubdirs);
                        GridData compositeTabSubdirsLData = new GridData();
                        compositeTabSubdirs
                            .setLayoutData(compositeTabSubdirsLData);
                        GridLayout compositeTabSubdirsLayout = new GridLayout();
                        compositeTabSubdirsLayout.numColumns = 2;
                        compositeTabSubdirsLayout.makeColumnsEqualWidth = false;
                        compositeTabSubdirs
                            .setLayout(compositeTabSubdirsLayout);
                        {
                            {
                                GridData directoryTreeLData = new GridData();
                                directoryTreeLData.grabExcessHorizontalSpace = true;
                                directoryTreeLData.grabExcessVerticalSpace = true;
                                directoryTreeLData.horizontalAlignment = GridData.FILL;
                                directoryTreeLData.verticalAlignment = GridData.FILL;
                                directoryTree = new Tree(
                                    compositeTabSubdirs,
                                    SWT.BORDER | SWT.SINGLE);
                                directoryTree.setSize(100, 100);
                                directoryTree
                                    .setLayoutData(directoryTreeLData);
                                directoryTree
                                    .addTreeListener(new TreeAdapter() {
                                        public void treeExpanded(
                                            TreeEvent evt) {
                                            TreeItem item = (TreeItem) evt.item;
                                            TreeItem[] childrens = item
                                                .getItems();
                                            for (int i = 0; i < childrens.length; i++) {
                                                if (childrens[i]
                                                    .getData(EXPANDED_KEY) == null) {
                                                    File file = (File) childrens[i]
                                                        .getData();
                                                    try {
                                                        addChildren(
                                                            file,
                                                            childrens[i]);
                                                    } catch (IOException e) {
                                                        ExceptionHandler
                                                            .reportException(e);
                                                    }
                                                    childrens[i].setData(
                                                        EXPANDED_KEY,
                                                        new Object());
                                                    //												updateDirectoryTree(childrens[i], item.getForeground());
                                                }
                                            }
                                        }
                                    });
                            }
                            {
                                Composite compositeButtons = new Composite(
                                    compositeTabSubdirs,
                                    SWT.NONE);
                                GridLayout compositeButtonsLayout = new GridLayout();
                                compositeButtonsLayout.makeColumnsEqualWidth = true;
                                GridData compositeButtonsLData = new GridData();
                                compositeButtonsLData.grabExcessVerticalSpace = true;
                                compositeButtonsLData.verticalAlignment = GridData.FILL;
                                compositeButtons
                                    .setLayoutData(compositeButtonsLData);
                                compositeButtons
                                    .setLayout(compositeButtonsLayout);
                                {
                                    Button buttonFilter = new Button(
                                        compositeButtons,
                                        SWT.PUSH | SWT.CENTER);
                                    buttonFilter.setText("Set Filter...");

                                    buttonFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(
                                                SelectionEvent arg0) {
                                                TreeItem[] selectedItems = directoryTree
                                                    .getSelection();

                                                if (selectedItems.length <= 0) {
                                                    return;
                                                }

                                                TreeItem selectedItem = selectedItems[0];
                                                FileFilter currentItemFilter = (FileFilter) selectedItem
                                                    .getData(FILTER_KEY);

                                                WizardDialog dialog = new WizardDialog(
                                                    getShell(),
                                                    SWT.APPLICATION_MODAL);
                                                FileFilterPage page = new FileFilterPage(
                                                    dialog,
                                                    currentItemFilter);
                                                dialog.show();
                                                FileFilter newfilter = page
                                                    .getFileFilter();
                                                if (newfilter != null) {
                                                    selectedItem.setData(
                                                        FILTER_KEY,
                                                        newfilter);
                                                    treeItemsWithFilter
                                                        .add(selectedItem);
                                                    File file = (File) selectedItem
                                                        .getData();
                                                    itemsMap.put(
                                                        file.getPath(),
                                                        newfilter);
                                                    markItem(selectedItem);
                                                }
                                            }
                                        });
                                }
                                {
                                    Button buttonRemoveFilter = new Button(
                                        compositeButtons,
                                        SWT.PUSH | SWT.CENTER);
                                    buttonRemoveFilter
                                        .setText("Remove Filter");

                                    buttonRemoveFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(
                                                SelectionEvent arg0) {
                                                TreeItem[] selectedItems = directoryTree
                                                    .getSelection();

                                                if (selectedItems.length <= 0) {
                                                    return;
                                                }

                                                TreeItem selectedItem = selectedItems[0];
                                                treeItemsWithFilter
                                                    .remove(selectedItem);
                                                File file = (File) selectedItem
                                                    .getData();
                                                itemsMap.remove(file
                                                    .getPath());
                                                unmarkItem(selectedItem);
                                            }
                                        });
                                }

                            }
                        }

                    }

                }
				sashForm.setWeights(new int[] {2,5});
			}
			comboType.add( "Publish/Update" ); //$NON-NLS-1$
			comboType.add( "Backup Copy" ); //$NON-NLS-1$
			comboType.add( "Exact Copy" ); //$NON-NLS-1$
			comboType.add( "Two Way Sync" ); //$NON-NLS-1$
			
			this.layout();
			this.setSize(609, 533);
			
			postInitGUI();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}
	
	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
		comboType.select(0);
		//	    comboPatternsType.select(0);
	}
	
	public void setProfileManager( ProfileManager manager )
	{
		this.profileManager = manager;
	}
	public void setProfileName( String name )
	{
		this.profileName = name;
		
		if( profileName == null )
			return;
		
		Profile p = profileManager.getProfile( profileName );
		if( p == null )
			throw new IllegalArgumentException( Messages.getString("ProfileDetails.profile_does_not_exist") ); //$NON-NLS-1$
		
		textName.setText( p.getName() );
		textDescription.setText( p.getDescription() );
		try {
            connectionConfigurationSource.setLocationDescription( new LocationDescription( p.getSource() ) );
        } catch( URISyntaxException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		buttonSourceBuffered.setSelection( "syncfiles".equals( p.getSource().getBufferStrategy() ) ); //$NON-NLS-1$
        try {
            connectionConfigurationDestination.setLocationDescription( new LocationDescription( p.getDestination() ) );
        } catch( URISyntaxException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		buttonDestinationBuffered.setSelection( "syncfiles".equals( p.getDestination().getBufferStrategy() ) ); //$NON-NLS-1$
		
		if( p.getSynchronizationType() != null && p.getSynchronizationType().length() > 0 )
			comboType.setText( p.getSynchronizationType() );
		else comboType.select( 0 );
		
		buttonScheduling.setData( p.getSchedule() );
		buttonEnabled.setSelection( p.isEnabled() );
		
		RuleSetDescriptor ruleSetDescriptor = p.getRuleSet();
		filter = null;
		
		if (ruleSetDescriptor instanceof SimplyfiedRuleSetDescriptor) {
			selectRuleSetButton(rbSimplyfiedRuleSet);
			rbSimplyfiedRuleSet.setSelection(true);
			rbAdvancedRuleSet.setSelection(false);
			SimplyfiedRuleSetDescriptor simpleDesc = (SimplyfiedRuleSetDescriptor)ruleSetDescriptor;
			syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
			FileFilter fileFilter = simpleDesc.getFileFilter();
			filter = fileFilter;
			if (fileFilter != null) {
				labelFilterDescription.setText(fileFilter.toString());
			}
			else {
				labelFilterDescription.setText("");
			}
			boolean useFilter = simpleDesc.isUseFilter();
			buttonUseFileFilter.setSelection(useFilter);
			if (useFilter) {
				label18.setEnabled(true);
				buttonFileFilter.setEnabled(true);
				labelFilterDescription.setEnabled(true);
			}
			else {
				label18.setEnabled(false);
				buttonFileFilter.setEnabled(false);
				labelFilterDescription.setEnabled(false);
			}
			FileFilterTree fileFilterTree = simpleDesc.getFileFilterTree();
			if (fileFilterTree != null) {
				itemsMap = fileFilterTree.getItemsMap();
			}
			else {
				itemsMap = new HashMap();
			}
		} else {
			selectRuleSetButton(rbAdvancedRuleSet);
			rbSimplyfiedRuleSet.setSelection(false);
			rbAdvancedRuleSet.setSelection(true);
			AdvancedRuleSetDescriptor advDesc = (AdvancedRuleSetDescriptor) ruleSetDescriptor;
			textRuleSet.setText(advDesc.getRuleSetName());
		}
	}
	
	private void drawDirectoryTree() {
		directoryTree.setRedraw(false);		
		directoryTree.removeAll();

		try {
			File rootFile = sourceSite.getRoot();
			Collection children = rootFile.getChildren();
			Iterator it = children.iterator();
			while (it.hasNext()) {
				File file = (File)it.next();
				if (file.isDirectory()) {
					TreeItem item = new TreeItem(directoryTree, SWT.NULL);
					item.setText(file.getName());
					item.setImage(GuiController.getInstance().getImage("Node_Directory.png"));
					item.setData(file);
					if (itemsMap.containsKey(file.getPath())) {
						markItem(item);
						treeItemsWithFilter.add(item);
						item.setData(FILTER_KEY, itemsMap.get(file.getPath()));
					}
					addChildren(file, item);
					item.setData(EXPANDED_KEY, new Object());
				}
			}
		} catch (IOException e) {
			ExceptionHandler.reportException(e);
		}
		
		directoryTree.setRedraw(true);
	}
	
	private void addChildren(File rootFile, TreeItem item) throws IOException {
		Collection children = rootFile.getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			File file = (File)it.next();
			if (file.isDirectory()) {
				TreeItem childrenItem = new TreeItem(item, SWT.NULL);
				childrenItem.setText(file.getName());
				childrenItem.setImage(GuiController.getInstance().getImage("Node_Directory.png"));
				childrenItem.setData(file);
				if (itemsMap.containsKey(file.getPath())) {
					markItem(childrenItem);
					treeItemsWithFilter.add(childrenItem);
					childrenItem.setData(FILTER_KEY, itemsMap.get(file.getPath()));
				}
			}
		}
	}

	private void markItem(TreeItem item) {
		String text = item.getText();
		if (text.charAt(0) != '*') {
			item.setText('*'+text);
		}
	}

	private void unmarkItem(TreeItem item) {
		String text = item.getText();
		if (text.charAt(0) == '*') {
			item.setText(text.substring(1));
		}
	}
	
	
//	private void updateDirectoryTree(TreeItem rootItem, Color defaultColor) {
//		Color theColor = defaultColor;
//		if (rootItem != null) {
//			if (includedDirs.contains(rootItem.getData())) {
//				theColor = selectedColor;
//			}
//			else if (excludedDirs.contains(rootItem.getData())) {
//				theColor = unselectedColor;
//			}
//			else {
//				theColor = defaultColor;
//			}
//			rootItem.setForeground(theColor);
//			
//			String text = rootItem.getText();
//			if (filterDirs.contains(rootItem.getData())) {
//				if (text.charAt(0) != '*') {
//					rootItem.setText('*'+text);
//				}
//			}
//			else {
//				if (text.charAt(0) == '*') {
//					rootItem.setText(text.substring(1));
//				}
//			}
//			
//			TreeItem[] childrens = rootItem.getItems();
//			for (int i = 0; i < childrens.length; i++) {
//				updateDirectoryTree(childrens[i], theColor);				
//			}
//		}
//		else {
//			TreeItem[] childrens = directoryTree.getItems();
//			for (int i = 0; i < childrens.length; i++) {
//				updateDirectoryTree(childrens[i], defaultColor);
//			}
//		}
//		
//	}
		
	public static void showProfile( Shell parent, ProfileManager manager, String name ){
		try {
			WizardDialog dialog = new WizardDialog( parent, SWT.APPLICATION_MODAL );
			ProfileDetailsPage page = new ProfileDetailsPage( dialog, manager, name );
			dialog.show();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	
	public void apply()
	{
		closeSourceSite();
		
		ConnectionDescription src, dst;
		try {
			src = getSourceConnection();
			dst = getDestinationConnection();
		} catch( Exception e ) {
			ExceptionHandler.reportException( e );
			return;
		}
		
		if( profileName == null || !textName.getText().equals( profileName ) )
		{
			Profile pr = profileManager.getProfile( textName.getText() );
			if( pr != null )
			{
				MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
				mb.setText( Messages.getString("ProfileDetails.Duplicate_Entry") ); //$NON-NLS-1$
				mb.setMessage( Messages.getString("ProfileDetails.Profile_already_exists") ); //$NON-NLS-1$
				mb.open();
				return;
			}
		}
		
		Profile p;
		
		RuleSetDescriptor ruleSetDescriptor = null;
		if (rbSimplyfiedRuleSet.getSelection()) {
			ruleSetDescriptor = new SimplyfiedRuleSetDescriptor(syncSubsButton.getSelection(), 
					filter,
					buttonUseFileFilter.getSelection(),
					getFileFilterTree());
		}
		if (rbAdvancedRuleSet.getSelection()) {
			String ruleSetName = textRuleSet.getText();
			ruleSetDescriptor = new AdvancedRuleSetDescriptor(ruleSetName);
		}
		
		if( profileName == null )
		{
			p = new Profile( textName.getText(), src, dst, ruleSetDescriptor );
			p.setSynchronizationType( comboType.getText() );
			p.setDescription( textDescription.getText() );
			p.setSchedule( (Schedule)buttonScheduling.getData() );
			p.setEnabled( buttonEnabled.getSelection() );
			if( buttonResetError.getSelection() )
				p.setLastError( 0, null );
			profileManager.addProfile( p );
		} else {
			p = profileManager.getProfile( profileName );
			p.beginUpdate();
			p.setName( textName.getText() );
			p.setDescription( textDescription.getText() );
			p.setSynchronizationType( comboType.getText() );
			p.setSource( src );
			p.setDestination( dst );
			p.setSchedule( (Schedule)buttonScheduling.getData() );
			p.setEnabled( buttonEnabled.getSelection() );
			
			p.setRuleSet( ruleSetDescriptor );
			if( buttonResetError.getSelection() )
				p.setLastError( 0, null );
			p.endUpdate();
		}
		profileManager.save();
	}
	
	private ConnectionDescription getDestinationConnection() {
		ConnectionDescription dst;
        try {
            dst = connectionConfigurationDestination.getLocationDescription().toConnectionDescription();
        } catch( URISyntaxException e ) {
            return null;
        }
		if( buttonDestinationBuffered.getSelection() )
			dst.setBufferStrategy( "syncfiles" ); //$NON-NLS-1$
		return dst;
	}

	private ConnectionDescription getSourceConnection() {
        ConnectionDescription dst = null;
        try {
            dst = connectionConfigurationSource.getLocationDescription().toConnectionDescription();
        } catch( URISyntaxException e ) {
            return null;
        }
        if( buttonSourceBuffered.getSelection() )
            dst.setBufferStrategy( "syncfiles" ); //$NON-NLS-1$
        return dst;
	}

	/** Auto-generated event handler method */
	protected void buttonCancelWidgetSelected(SelectionEvent evt){
		closeSourceSite();
		getShell().dispose();
	}
	
	
	protected void selectRuleSetButton(Button button) {
		
		if (button.equals(rbSimplyfiedRuleSet)) {
			advancedRuleOptionsGroup.setEnabled(false);
			label4.setEnabled(false);
			textRuleSet.setEnabled(false);
			simplyfiedOptionsGroup.setEnabled(true);
			syncSubsButton.setEnabled(true);
		}
		else {
			advancedRuleOptionsGroup.setEnabled(true);
			label4.setEnabled(true);
			textRuleSet.setEnabled(true);
			simplyfiedOptionsGroup.setEnabled(false);
			syncSubsButton.setEnabled(false);
		}
		
	}
	
	private void treeTabsWidgetSelected(SelectionEvent evt) {
		((StackLayout)compositeMain.getLayout()).topControl = 
			(Composite)treeTabs.getSelection()[0].getData();
		compositeMain.layout();
		
		if (treeTabs.getSelection()[0] == treeItemSubDirs) {
			if (sourceSite == null) {
				directoryTree.removeAll();
				TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
				loadingIem.setText("Loading source dir...");
				loadingIem.setImage(GuiController.getInstance().getImage("Node_Directory.png"));
				
				Display display = Display.getCurrent();
				display.asyncExec(new Runnable() {
					public void run() {
						ConnectionDescription src = getSourceConnection();
						try {
							sourceSite = fsm.createConnection(src);
							drawDirectoryTree();
						} catch (FileSystemException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						} catch (IOException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						} catch (URISyntaxException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						} catch (net.sourceforge.fullsync.FileSystemException e) {
							// FIXME Jan can you check this? I had to add it after an update. I have probably
							// made a mess with the merge.
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						}
					}	
				});
			}
			
		}

	}

	private FileFilterTree getFileFilterTree() {
		int numOfNodes = treeItemsWithFilter.size();
		FileFilterTree fileFilterTree = new FileFilterTree();
		for (int i = 0; i < numOfNodes; i++) {
			TreeItem item = (TreeItem)treeItemsWithFilter.get(i);
			FileFilter itemFilter = (FileFilter)item.getData(FILTER_KEY);
			File itemFile = (File)item.getData();
			fileFilterTree.addFileFilter(itemFile.getPath(), itemFilter);
		}
		
		return fileFilterTree;
	}
	
	private void closeSourceSite() {
		if (sourceSite != null) {
			try {
				sourceSite.close();
				sourceSite = null;
			} catch (IOException e) {
			}
		}
	}
}
