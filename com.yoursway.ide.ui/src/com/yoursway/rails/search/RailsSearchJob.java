package com.yoursway.rails.search;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;

import com.yoursway.ide.ui.Activator;
import com.yoursway.rails.RailsRuntime;
import com.yoursway.rubygems.LocalGems;
import com.yoursway.utils.InterpreterRunnerUtil;

/**
 * Searches for all Rails instances in single Ruby installation, and
 * triggers the getting information about each installed Rails instance.
 * 
 * FIXME: move most of the code to the separate class.
 * 
 * FIXME: outer try/catch is a workaround: sometimes ruby1.9 process reports
 * exit code 0 instead of 3.
 */
public class RailsSearchJob extends Job {
    private final IInterpreterInstall rubyInterpreter;
    
    RailsSearchJob(IInterpreterInstall rubyInterpreter) {
        super("Searching Rails in " + rubyInterpreter.getName());
        this.rubyInterpreter = rubyInterpreter;
    }
    
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            SubMonitor progress = SubMonitor.convert(monitor);
            
            URL r = Activator.getDefault().getBundle().getEntry("scripts/simple_loader.rb");
            String fileName = null;
            try {
                fileName = new File(FileLocator.toFileURL(r).getFile()).getAbsolutePath();
                // without new File(...).getAbsolutePath() on windows it
                // returns path /C:/Ruby/... which ruby.exe don't understand
            } catch (IOException e) {
                // FIXME
                return Status.CANCEL_STATUS;
            }
            
            InterpreterConfig config = new InterpreterConfig(new File(fileName));
            config.addScriptArgs(new String[] { "rails" });
            
            ILaunch launch = null;
            try {
                launch = InterpreterRunnerUtil.run(rubyInterpreter, config, progress
                        .newChild(IProgressMonitor.UNKNOWN));
            } catch (CoreException c) {
                // FIXME
            }
            
            if (launch == null)
                return Status.CANCEL_STATUS;
            
            IProcess[] launchProcesses = launch.getProcesses();
            assert launchProcesses.length == 1 : "Ruby script launch is expected to have single process";
            
            IProcess launchProcess = launchProcesses[0];
            
            System.out.println(getName() + " exit value: "
                    + String.valueOf(InterpreterRunnerUtil.getFinishedProcessExitValue(launchProcess)));
            
            // FIXME: proper error hadling
            String errorMsg = RailsRuntime.checkFinishedProcess(launchProcess, getName());
            if (errorMsg != null) {
                System.out.println(getName() + " " + errorMsg);
                return Status.OK_STATUS;
            }
            
            String gemsInfo = launchProcess.getStreamsProxy().getOutputStreamMonitor().getContents();
            System.out.println(getName() + " " + gemsInfo);
            
            String[] installedRailsGemsVersions = LocalGems.parseGemVersions(gemsInfo);
            
            System.out.println(getName());
            for (String installedRailsGemVersion : installedRailsGemsVersions)
                System.out.println(installedRailsGemVersion);
            
            for (String installedRailsGemVersion : installedRailsGemsVersions)
                new RailsRegisterJob(rubyInterpreter, installedRailsGemVersion).schedule();
            
        } catch (Throwable t) {
            System.out.println(getName());
            t.printStackTrace(System.out);
        }
        
        return Status.OK_STATUS;
    }
}