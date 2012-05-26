package edu.ucsb.deepspace.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ucsb.deepspace.business.ActuatorCommand;
import edu.ucsb.deepspace.business.Bookkeeper;
import edu.ucsb.deepspace.business.Mediator;
import edu.ucsb.deepspace.business.TrackerCommands;
import edu.ucsb.deepspace.business.TrackerMeasureMode;
public class MMT4MainWindow extends org.eclipse.swt.widgets.Composite {
	
	private Shell shell;
	private final Properties appSettings = new Properties();
	private Mediator mediator;
	
	private Button test;
    private Button varTest;
    private Button actReadEeprom;
    private Button actLed;
    private Button actReboot;
    private Button actExternalTemp;
    private Button actInternalTemp;
    private Group statusAreaGroup;
    private Button actGetStatus;
    private Button actMotorOff;
    private Button actMotorReallyOff;
    private Button actMotorOn;
    private Button splitTest;
    
    private Group testsGroup;
    private Text comments;
    private Button beginTest;
    private Button actFastSample;
    private Button actTestAndCorrect;
    private Button singleReflTest;
    private int testTypeFlag = 0;
    
    private Group measurementGrp;
    private Button stop;
    private Label newPointEvery;
    private Text numSeconds;
    private Label seconds;
    private Text numSampPerPoint;
    private Label samplesPerPoint;
    private Button servoOnBox;
    private Button start;
    private static Text measurementStatus;
    
    private Group otherCommands;
    private Button othMakeGraph;
    private Button othImportReflectables;
    private Button othExportReflectables;

    private Group targetsGrp;
    private static org.eclipse.swt.widgets.List targets;
    private Group actuatorsGrp;
    private static org.eclipse.swt.widgets.List actuators;
    private static Text reflFilename;
    private Label reflectablesName;
    
    private static Group trackerCommands;
    private static Button trkGoToRefl;
    private static Button trkConnect;
    private static Button trkDisconnect;
    private static Button trkStartupChecks;
    private static Button trkInitialize;
    private static Button trkWeatherInfo;
    private static Button trkAdm;
    private static Button trkIfm;
    private static Button trkIfmSetByAdm;
    private static Button trkHome;
    private static Button trkMove;
    private static Button trkAbort;
    private static Button trkSaveRefl;
    private static Button trkSearch;
    private static Button trkUpdateRefl;
    private static Button trkEditRefl;
    private static Text trkReflName;
    private static Text trkPhiVal;
    private static Text trkThetaVal;
    private static Text trkSearchRadiusVal;
    private static Button trkDelRefl;
    private static Button trkCompensate;
    private static Button trkHealthChecks;
    private static Button trkTarType;
    
    private Group actuatorCommands;
    private Button actCalibrate;
    private Button actSendCommand;
    private Button actSilenceActuators;
    private Label actLabSteps;
    private Text actSteps;
    
    private static Text statusArea;
 
	private boolean startFlag = false;

	public MMT4MainWindow(Composite parent, int style, Mediator mediator) {
		super(parent, style);
		this.mediator = mediator;
		shell = parent.getShell();
		setPreferences();
		initGUI();
		controlTrkButtons(false);
		trkConnect.setEnabled(true);
		shell.setLayout(new FillLayout());
        shell.setText("MMT Control");
        shell.layout();
		shell.open();
	}
	
	public void alive() {
		while (!shell.isDisposed()) {
            if (!Display.getDefault().readAndDispatch())
            	Display.getDefault().sleep();
        }
	}
	
	private void initGUI() {
		getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent evt) {shellWidgetDisposed();}
		});

		getShell().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent evt) {saveShellBounds();}
			public void controlMoved(ControlEvent evt) {saveShellBounds();}
		});
		
		guiTracker();
		guiActuator();
		guiMeasurement();
		guiTests();
		
		statusAreaGroup = new Group(this, SWT.NONE);
		statusAreaGroup.setLayout(null);
		statusAreaGroup.setText("Status Area");
		statusAreaGroup.setBounds(19, 348, 320, 133);

		statusArea = new Text(statusAreaGroup, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		statusArea.setBounds(12, 20, 296, 101);

		actuatorsGrp = new Group(this, SWT.NONE);
		actuatorsGrp.setText("Actuators");
		actuatorsGrp.setLayout(null);
		actuatorsGrp.setBounds(356, 18, 128, 148);
		actuators = new org.eclipse.swt.widgets.List(actuatorsGrp, SWT.NONE);
		actuators.setBounds(12, 19, 100, 114);
		actuators.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targets.deselectAll();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		targetsGrp = new Group(this, SWT.NONE);
		targetsGrp.setLayout(null);
		targetsGrp.setText("Targets");
		targetsGrp.setBounds(356, 173, 128, 136);
		targets = new org.eclipse.swt.widgets.List(targetsGrp, SWT.NONE);
		targets.setBounds(12, 20, 100, 104);
		targets.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actuators.deselectAll();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		otherCommands = new Group(this, SWT.NONE);
		otherCommands.setText("Other Commands");
		otherCommands.setLayout(null);
		otherCommands.setBounds(496, 227, 308, 55);

		othImportReflectables = new Button(otherCommands, SWT.PUSH | SWT.CENTER);
		othImportReflectables.setText("Import Reflectables");
		othImportReflectables.setBounds(9, 19, 103, 30);
		othImportReflectables.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				//String filename = dialog.open();
				//mediator.importRefl(othImportReflectables, filename);
			}
		});

		othExportReflectables = new Button(otherCommands, SWT.PUSH | SWT.CENTER);
		othExportReflectables.setText("Export Reflectables");
		othExportReflectables.setBounds(115, 19, 100, 31);
		othExportReflectables.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				//String filename = dialog.open();
				//mediator.exportRefl(othExportReflectables, filename);
			}
		});

		othMakeGraph = new Button(otherCommands, SWT.PUSH | SWT.CENTER);
		othMakeGraph.setText("Make Graph");
		othMakeGraph.setBounds(221, 20, 63, 30);
		othMakeGraph.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.addOthJob(new MakeGraph(), othMakeGraph);
			}
		});


		reflectablesName = new Label(this, SWT.NONE);
		reflectablesName.setText("Reflectables Filename");
		reflectablesName.setBounds(356, 321, 128, 14);

		reflFilename = new Text(this, SWT.NONE);
		reflFilename.setBounds(356, 341, 128, 30);
		reflFilename.setEditable(false);
	}

	private void guiTracker() {
		trackerCommands = new Group(this, SWT.NONE);
		trackerCommands.setText("Tracker Commands");
		trackerCommands.setLayout(null);
		trackerCommands.setBounds(19, 18, 320, 318);

		trkConnect = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkConnect.setText("Connect");
		trkConnect.setBounds(12, 24, 80, 30);
		trkConnect.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkConnect, TrackerCommands.CONNECT);
				trkConnect.setEnabled(false);
			}
		});

		trkDisconnect = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkDisconnect.setText("Disconnect");
		trkDisconnect.setBounds(99, 24, 60, 30);
		trkDisconnect.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkDisconnect, TrackerCommands.DISCONNECT);
				trkDisconnect.setEnabled(false);
			}
		});

		trkStartupChecks = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkStartupChecks.setText("Startup Checks");
		trkStartupChecks.setBounds(12, 59, 80, 30);
		trkStartupChecks.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkStartupChecks, TrackerCommands.STARTUPCHECKS);
				trkStartupChecks.setEnabled(false);
				//mediator.trackerCommand(, TrackerCommands);
			}
		});

		trkInitialize = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkInitialize.setText("Initialize");
		trkInitialize.setBounds(165, 24, 60, 30);
		trkInitialize.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.trackerCommand(trkInitialize, TrackerCommands);
				trkInitialize.setEnabled(false);
			}
		});

		trkWeatherInfo = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkWeatherInfo.setText("Weather Info");
		trkWeatherInfo.setBounds(98, 167, 84, 30);
		trkWeatherInfo.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkWeatherInfo, TrackerCommands.WEATHER);
				trkWeatherInfo.setEnabled(false);
			}
		});

		trkAdm = new Button(trackerCommands, SWT.RADIO | SWT.LEFT);
		trkAdm.setText("ADM");
		trkAdm.setBounds(150, 131, 45, 30);
		trkAdm.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.setMeasureMode(TrackerMeasureMode.ADM, trkAdm);
				trkAdm.setSelection(false);
			}
		});

		trkIfm = new Button(trackerCommands, SWT.RADIO | SWT.LEFT);
		trkIfm.setText("IFM");
		trkIfm.setBounds(98, 131, 40, 30);
		trkIfm.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.setMeasureMode(TrackerMeasureMode.IFM, trkIfm);
				trkIfm.setSelection(false);
			}
		});

		trkIfmSetByAdm = new Button(trackerCommands, SWT.RADIO | SWT.LEFT);
		trkIfmSetByAdm.setText("IFM set by ADM");
		trkIfmSetByAdm.setBounds(98, 95, 97, 30);
		trkIfmSetByAdm.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.setMeasureMode(TrackerMeasureMode.IFMBYADM, trkIfmSetByAdm);
				trkIfmSetByAdm.setSelection(false);
			}
		});

		trkHome = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkHome.setText("Home");
		trkHome.setBounds(12, 131, 80, 30);
		trkHome.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkHome, TrackerCommands.HOME);
				trkHome.setEnabled(false);
			}
		});

		trkMove = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkMove.setText("Move");
		trkMove.setBounds(12, 276, 60, 30);
		trkMove.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.move(4D, Double.parseDouble(trkThetaVal.getText()), Double.parseDouble(trkPhiVal.getText()), trkMove);
				trkMove.setEnabled(false);
			}
		});

		trkAbort = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkAbort.setText("Abort");
		trkAbort.setBounds(99, 59, 60, 30);
		trkAbort.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkAbort, TrackerCommands.ABORT);
				trkAbort.setEnabled(false);
			}
		});

		trkGoToRefl = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkGoToRefl.setText("Go To Refl.");
		trkGoToRefl.setBounds(231, 24, 80, 30);
		trkGoToRefl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				if (getSelectedRefl() == null) statusArea.append("A reflectable must be selected.\n");
				//mediator.goToRef(getSelectedRefl(), trkGoToRefl);
				trkGoToRefl.setEnabled(false);
			}
		});

		trkSaveRefl = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkSaveRefl.setText("Save Refl.");
		trkSaveRefl.setBounds(243, 59, 68, 30);
		trkSaveRefl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.saveReflectable(trkSaveRefl, trkReflName.getText());
				trkSaveRefl.setEnabled(false);
			}
		});

		trkSearch = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkSearch.setText("Search");
		trkSearch.setBounds(12, 243, 80, 28);
		trkSearch.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.search(Double.parseDouble(trkSearchRadiusVal.getText()), trkSearch);
				trkSearch.setEnabled(false);
			}
		});

		trkUpdateRefl = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkUpdateRefl.setText("Update Refl. Position");
		trkUpdateRefl.setBounds(194, 167, 113, 30);
		trkUpdateRefl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.updateReflPos(getSelectedRefl(), trkUpdateRefl);
				trkUpdateRefl.setEnabled(false);
			}
		});

		trkDelRefl = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkDelRefl.setText("Delete Refl.");
		trkDelRefl.setBounds(228, 131, 80, 30);
		trkDelRefl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//bk.deleteRefl(getSelectedRefl());
			}
		});

		trkTarType = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkTarType.setText("Target Type");
		trkTarType.setBounds(12, 95, 80, 30);
		trkTarType.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkTarType, TrackerCommands.TARGETTYPE);
				trkTarType.setEnabled(false);
			}
		});

		trkHealthChecks = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkHealthChecks.setText("Health Checks");
		trkHealthChecks.setBounds(12, 167, 80, 30);
		trkHealthChecks.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.trackerCommand(trkHealthChecks, TrackerCommands.HEALTHCHECKS);
				trkHealthChecks.setEnabled(false);
			}
		});

		trkCompensate = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkCompensate.setText("Compensate");
		trkCompensate.setBounds(164, 59, 67, 31);
		trkCompensate.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.compensation(getSelectedRefl(), trkCompensate);
				trkCompensate.setEnabled(false);
			}
		});

		trkSearchRadiusVal = new Text(trackerCommands, SWT.NONE);
		trkSearchRadiusVal.setText("radius (m)");
		trkSearchRadiusVal.setBounds(104, 249, 55, 15);

		trkThetaVal = new Text(trackerCommands, SWT.NONE);
		trkThetaVal.setText("theta (rad)");
		trkThetaVal.setBounds(92, 276, 63, 16);

		trkPhiVal = new Text(trackerCommands, SWT.NONE);
		trkPhiVal.setText("phi (rad)");
		trkPhiVal.setBounds(167, 276, 60, 16);

		trkReflName = new Text(trackerCommands, SWT.NONE);
		trkReflName.setText("Reflectable name");
		trkReflName.setBounds(217, 95, 92, 16);

		trkEditRefl = new Button(trackerCommands, SWT.PUSH | SWT.CENTER);
		trkEditRefl.setText("Edit Refl.");
		trkEditRefl.setBounds(248, 203, 60, 30);
		trkEditRefl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.compensation(getSelectedRefl(), editRefl);
				//editRefl.setEnabled(false);
			}
		});
	}
	
	private void guiActuator() {
		actuatorCommands = new Group(this, SWT.NONE);
		actuatorCommands.setText("Actuator Commands");
		actuatorCommands.setLayout(null);
		actuatorCommands.setBounds(496, 18, 308, 203);

		actSilenceActuators = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actSilenceActuators.setText("Silence Actuators");
		actSilenceActuators.setBounds(140, 167, 96, 31);
		actSilenceActuators.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.silenceActuators(actSilenceActuators);
				actSilenceActuators.setEnabled(false);
			}
		});

		actCalibrate = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actCalibrate.setText("Calibrate");
		actCalibrate.setBounds(126, 21, 60, 30);
		actCalibrate.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.calibrate(getSelectedAct(), actCalibrate);
				actCalibrate.setEnabled(false);
			}
		});

		actSendCommand = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actSendCommand.setText("Move Relative");
		actSendCommand.setBounds(12, 57, 96, 30);
		actSendCommand.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				mediator.sendCommand(getSelectedAct(), ActuatorCommand.MOVERELATIVE, actSteps.getText(), actSendCommand);
			}
		});

		actSteps = new Text(actuatorCommands, SWT.SINGLE);
		actSteps.setBounds(48, 27, 57, 19);

		actLabSteps = new Label(actuatorCommands, SWT.NONE);
		actLabSteps.setText("Steps:");
		actLabSteps.setBounds(12, 27, 36, 14);

		servoOnBox = new Button(actuatorCommands, SWT.CHECK | SWT.LEFT);
		servoOnBox.setSelection(false);
		servoOnBox.setText("Servo On/Off");
		servoOnBox.setBounds(147, 144, 86, 17);
		servoOnBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				//bk.toggleServoOnFlag();
			}
		});

		actMotorOn = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actMotorOn.setText("Motor On");
		actMotorOn.setBounds(166, 57, 63, 30);
		actMotorOn.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Motor On", "0", actMotorOn);
			}
		});

		actMotorReallyOff = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actMotorReallyOff.setText("Motor Really Off");
		actMotorReallyOff.setBounds(198, 21, 97, 30);
		actMotorReallyOff.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Motor Really Off", "0", actMotorReallyOff);
			}
		});

		actMotorOff = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actMotorOff.setText("Motor Off");
		actMotorOff.setBounds(234, 57, 61, 29);
		actMotorOff.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Motor Off", "0", actMotorOff);
			}
		});

		actGetStatus = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actGetStatus.setText("Get Status");
		actGetStatus.setBounds(12, 167, 63, 30);
		actGetStatus.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Get Status", "0", actGetStatus);
			}
		});

		actInternalTemp = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actInternalTemp.setText("Internal Temp");
		actInternalTemp.setBounds(12, 95, 96, 30);
		actInternalTemp.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Internal Temp", "0", actInternalTemp);
			}
		});

		actExternalTemp = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actExternalTemp.setText("External Temp");
		actExternalTemp.setBounds(12, 131, 96, 30);
		actExternalTemp.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "External Temp", "0", actExternalTemp);
			}
		});

		actReboot = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actReboot.setText("Reboot");
		actReboot.setBounds(234, 95, 61, 29);
		actReboot.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Reboot", "0", actReboot);
			}
		});

		actLed = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actLed.setText("LED");
		actLed.setBounds(124, 57, 36, 30);
		actLed.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//System.out.println(mediator.getReflectables().get(getSelectedAct()).toCSV());
				//mediator.sendCommand(getSelectedAct(), "LED", actSteps.getText(), actLed);
			}
		});

		actReadEeprom = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		actReadEeprom.setText("Read EEPROM");
		actReadEeprom.setBounds(142, 95, 80, 30);
		actReadEeprom.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.sendCommand(getSelectedAct(), "Read EEPROM", "0", actReadEeprom);
			}
		});

		//final TempMonitor asdf = new TempMonitor();
		//asdf.start();
		test = new Button(actuatorCommands, SWT.PUSH | SWT.CENTER);
		test.setText("test");
		test.setBounds(262, 167, 39, 26);
		test.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				//mediator.test(getSelectedAct());
				mediator.music();
				//asdf.tempsAsString();
			}
		});
	}
	
	private void guiMeasurement() {
		measurementGrp = new Group(this, SWT.NONE);
		measurementGrp.setLayout(null);
		measurementGrp.setText("Measurment");
		measurementGrp.setBounds(502, 288, 220, 190);

		stop = new Button(measurementGrp, SWT.PUSH | SWT.CENTER);
		stop.setText("Stop");
		stop.setBounds(78, 82, 60, 30);
		stop.setEnabled(false);
		stop.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				stop.setEnabled(false);
				start.setEnabled(true);
				startFlag = false;
				//mediator.addTrkJob(new StopMeasure(), null);
			}
		});

		start = new Button(measurementGrp, SWT.PUSH | SWT.CENTER);
		start.setText("Start");
		start.setBounds(8, 82, 60, 30);
		start.setEnabled(true);
		start.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				if (numSeconds.getText().equals("") || numSampPerPoint.getText().equals("") || getSelectedAct().equals("")) {
					System.out.println("All fields must be filled out before starting to measure.");
				}
				else {
					start.setEnabled(false);
					stop.setEnabled(true);
					startFlag = true;
					modified();
				}
			}
		});

		samplesPerPoint = new Label(measurementGrp, SWT.NONE);
		samplesPerPoint.setText("Samples per Point");
		samplesPerPoint.setBounds(8, 54, 87, 16);

		newPointEvery = new Label(measurementGrp, SWT.NONE);
		newPointEvery.setText("New Point Every");
		newPointEvery.setBounds(8, 28, 84, 14);

		numSampPerPoint = new Text(measurementGrp, SWT.NONE);
		numSampPerPoint.setText("10");
		numSampPerPoint.setBounds(101, 54, 58, 16);

		numSeconds = new Text(measurementGrp, SWT.NONE);
		numSeconds.setText("1");
		numSeconds.setBounds(97, 28, 59, 14);

		seconds = new Label(measurementGrp, SWT.NONE);
		seconds.setText("Seconds");
		seconds.setBounds(167, 28, 40, 14);

		measurementStatus = new Text(measurementGrp, SWT.READ_ONLY);
		measurementStatus.setBounds(11, 118, 194, 57);
		measurementStatus.setText("Azimuth: \n" +
				"Zenith: \n" +
				"Distance: \n" +
				"Error: ");
		//measurementStatus.setFont(SWTResourceManager.getFont("Courier", 8, 0, false, false));
	}

	private void guiTests() {
		testsGroup = new Group(this, SWT.NONE);
		testsGroup.setLayout(null);
		testsGroup.setText("Tests");
		testsGroup.setBounds(24, 490, 460, 109);

		actTestAndCorrect = new Button(testsGroup, SWT.RADIO | SWT.CENTER);
		actTestAndCorrect.setText("Multi Refl Test");
		actTestAndCorrect.setBounds(7, 20, 103, 30);
		actTestAndCorrect.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				testTypeFlag = 1;
			}
		});

		actFastSample = new Button(testsGroup, SWT.RADIO | SWT.CENTER);
		actFastSample.setText("Fast Sample");
		actFastSample.setBounds(7, 50, 96, 30);
		actFastSample.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				testTypeFlag = 2;
			}
		});

		singleReflTest = new Button(testsGroup, SWT.RADIO | SWT.CENTER);
		singleReflTest.setText("Single Refl Test");
		singleReflTest.setBounds(7, 80, 96, 30);
		singleReflTest.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				testTypeFlag = 3;
			}
		});

		splitTest = new Button(testsGroup, SWT.RADIO | SWT.LEFT);
		splitTest.setText("Split Test");
		splitTest.setBounds(122, 20, 61, 23);
		splitTest.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				testTypeFlag = 4;
			}
		});

		varTest = new Button(testsGroup, SWT.RADIO | SWT.LEFT);
		varTest.setText("Var Test");
		varTest.setBounds(122, 50, 59, 17);
		varTest.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				testTypeFlag = 5;
			}
		});

		beginTest = new Button(testsGroup, SWT.PUSH | SWT.CENTER);
		beginTest.setText("Begin Test");
		beginTest.setBounds(123, 71, 60, 30);
		beginTest.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent evt) {
				if (testTypeFlag == 0) {
					statusArea.append("Please select a test to begin.\n");
					return;
				}

				if (testTypeFlag == 1) {
					//mediator.addActJob(new MultiReflTest(comments.getText()), beginTest);
				}
				else if (testTypeFlag == 2) {
					//mediator.addActJob(new FastSample(comments.getText(), getSelectedAct()), beginTest);
				}
				else if (testTypeFlag == 3) {
					//mediator.addActJob(new SingleReflTest(comments.getText(), getSelectedAct()), beginTest);
				}
				else if (testTypeFlag == 4) {
					//mediator.addOthJob(new SplitTest(comments.getText()), beginTest);
				}
				else if (testTypeFlag == 5) {
					//mediator.addOthJob(new VarTest(comments.getText()), beginTest);
				}
				beginTest.setEnabled(false);
			}
		});

		comments = new Text(testsGroup, SWT.MULTI | SWT.WRAP);
		comments.setText("Comments for test go here...");
		comments.setBounds(201, 20, 247, 75);
	}
	
	
	/**
     * Load size of window from file.
     */
    private void setPreferences() {
        try {
            appSettings.load(new FileInputStream("appsettings.ini"));
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }

        int width = Integer.parseInt(appSettings.getProperty("width", "800"));
        int height = Integer.parseInt(appSettings.getProperty("height", "600"));
        Rectangle screenBounds = getDisplay().getBounds();
        int defaultTop = (screenBounds.height - height) / 2;
        int defaultLeft = (screenBounds.width - width) / 2;
        int top = Integer.parseInt(appSettings.getProperty("top", String.valueOf(defaultTop)));
        int left = Integer.parseInt(appSettings.getProperty("left", String.valueOf(defaultLeft)));
        getShell().setSize(width, height);
        getShell().setLocation(left, top);
        saveShellBounds();
    }
    
    /**
     * Save window size and location to <code>appSettings</code>.
     */
    private void saveShellBounds() {
        Rectangle bounds = getShell().getBounds();
        appSettings.setProperty("top", String.valueOf(bounds.y));
        appSettings.setProperty("left", String.valueOf(bounds.x));
        appSettings.setProperty("width", String.valueOf(bounds.width));
        appSettings.setProperty("height", String.valueOf(bounds.height));
    }
    
    /**
     * Store <code>appSettings</code> to a file.
     */
    private void shellWidgetDisposed() {
        try {
        	saveShellBounds();
            appSettings.store(new FileOutputStream("appsettings.ini"), "");
            mediator.kill();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
    }
	
    
    /**
     * Gets the selected reflectable from either <code>actuators</code> or <code>targets</code>.
     * @return the name of the selected reflectable, or "" if nothing is selected
     */
    private String getSelectedRefl() {
        if (actuators.getSelectionCount() != 0) {
            return actuators.getItem(actuators.getSelectionIndex());
        } else if (targets.getSelectionCount() != 0) {
            return targets.getItem(targets.getSelectionIndex());
        }
        return null;
    }

    /**
     * Get the actuator that is selected.
     * @return the name of the selected actuator
     */
    String getSelectedAct() {
        if (actuators.getSelectionCount() == 0) {
            return null;
        } else {
            return actuators.getItem(actuators.getSelectionIndex());
        }
    }
    
    private void modified() {
		try {
			if (!numSeconds.getText().equals("") && !numSampPerPoint.getText().equals("") && !getSelectedAct().equals("")) {
				
				//Double valNumSeconds = Double.parseDouble(numSeconds.getText());
				//Double valNumSampPerPoint = Double.parseDouble(numSampPerPoint.getText());
				//String name = getSelectedAct();

				if (startFlag) {
					//mediator.addTrkJob(new StartMeasure(name, valNumSeconds, valNumSampPerPoint), null);
				}
			}
		}catch (NumberFormatException e) {
			System.out.println("NUMBER");
		}
	}
    
    private static void controlTrkButtons(boolean onOff) {
		Control[] temp = trackerCommands.getChildren();
		for (Control c : temp) {
			c.setEnabled(onOff);
		}
    }
    
    public static void updateStatusArea(String s) {
    	statusArea.append(s);
    	if (Bookkeeper.getInstance().getTrkCon()) {
    		controlTrkButtons(true);
    		trkConnect.setEnabled(false);
    	}
    	else if (!Bookkeeper.getInstance().getTrkCon()) {
    		controlTrkButtons(false);
    		trkConnect.setEnabled(true);
    	}
    }
    
    public static void updateMeasurementStatus(String s) {
    	measurementStatus.setText(s);
    }
    
    public static void updateMeasMode(String s) {
    	trkAdm.setSelection(false);
		trkIfm.setSelection(false);
		trkIfmSetByAdm.setSelection(false);
		
		trkAdm.setEnabled(true);
    	trkIfm.setEnabled(true);
    	trkIfmSetByAdm.setEnabled(true);
		
    	TrackerMeasureMode mode = Bookkeeper.getInstance().getTrkMeasmode();
    	switch (mode) {
			case IFM:
				trkIfm.setSelection(true);
	    		trkIfm.setEnabled(false);
				break;
			case ADM:
				trkAdm.setSelection(true);
	    		trkAdm.setEnabled(false);
				break;
			case IFMBYADM:
				trkIfmSetByAdm.setSelection(true);
	    		trkIfmSetByAdm.setEnabled(false);
				break;
			default:
				assert false; //There are only three measurement modes.
    	}
    }
    
    public static void updateTargetList(List<String> targetList) {
    	targets.removeAll();
    	for (String name : targetList){
    		targets.add(name);
    	}
    }
    
    public static void updateActuatorList(List<String> actuatorList) {
    	actuators.removeAll();
    	for (String name : actuatorList) {
    		actuators.add(name);
    	}
    }
    
    public static void updateReflFilename(String s) {
    	reflFilename.setText(s);
    }
}