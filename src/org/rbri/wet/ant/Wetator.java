/*
 * Copyright (c) 2008-2010 Ronald Brill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.rbri.wet.ant;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.rbri.wet.Version;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.exception.WetException;



/**
 * The AntTask to execute test within an ant script.
 *
 * @author rbri
 */
public class Wetator extends Task {
    private String config = null;
    private Path classpath = null;
    private FileSet fileset = null;

    /**
     * The main method called by Ant.
     */
    public void execute() {
        // check the input

        // config is required
        if (null == getConfig()) {
            throw new BuildException(Version.getProductName() + " Ant: Config-File is required (set property config).");
        }

        if (null == getFileset()) {
            throw new BuildException(Version.getProductName() + " Ant: Fileset is required (define a fileset for all your test files).");
        }

        // read the properties for us
        Hashtable<String, String> tmpProperties = (Hashtable<String, String>)getProject().getProperties();
        HashMap<String, String> tmpOurProperties = new HashMap<String, String>();
        Set<String> tmpKeys = tmpProperties.keySet();
        for (String tmpKey : tmpKeys) {
            if (tmpKey.startsWith(WetConfiguration.PROPERTY_PREFIX)) {
                tmpOurProperties.put(tmpKey, tmpProperties.get(tmpKey));
            }
        }

        try {
            WetEngine tmpWetEngine = new WetEngine();
            if (classpath != null) {
                // AntClassLoader
                ClassLoader tmpClassLoader = getProject().createClassLoader(getProject().getCoreLoader(), classpath);
                tmpWetEngine.setClassLoader(tmpClassLoader);
            }
            tmpWetEngine.setConfigFileName(getConfig());
            tmpWetEngine.setExternalProperties(tmpOurProperties);
            AntOutProgressListener tmpListener = new AntOutProgressListener(this);
            tmpWetEngine.addProgressListener(tmpListener);
            tmpWetEngine.init();

            // add all files
            DirectoryScanner tmpDirScanner = getFileset().getDirectoryScanner(getProject());
            String[] tmpListOfFiles = tmpDirScanner.getIncludedFiles();

            for (int i = 0; i < tmpListOfFiles.length; i++) {
                tmpWetEngine.addTestFile(new File(tmpDirScanner.getBasedir(), tmpListOfFiles[i]));
            }

            tmpWetEngine.executeTests();
        } catch (WetException e) {
            throw new BuildException(Version.getProductName() + " Ant: Task failed. (" + e.getMessage() + ")", e);
        }
    }


    protected String getConfig() {
        return config;
    }


    public void setConfig(String aConfig) {
        config = aConfig;
    }


    protected FileSet getFileset() {
        return fileset;
    }


    public FileSet createFileSet() {
        fileset = new FileSet();
        return fileset;
    }


    public Path createClasspath() {
        log("createClasspath", Project.MSG_ERR);

        if (null == classpath) {
            classpath = new Path(getProject());
        }
        return classpath;
    }
}
