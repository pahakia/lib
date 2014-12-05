lib
===

A collection of java libraries including:

<ol>
<li><b>pahakia-fault</b><br>
   Fault - the once-for-all exception, see blog: http://tri-katch.blogspot.com.</li>
<li><b>pahakia-apt</b><br>
   Annotation processing at build/compile time.<br>
   All annotated classes are recorded in META-INF/annotated-classes.<br>
   It works both with Eclipse and Maven.</li>
<li><b>pahakia-annotation-registry</b><br>
   Build a registry from all META-INF/annotated-classes on classpath, keyed by annotation name.</li>
<li><b>pahakia-settings</b><br>
   Centralize program settings in one place: Settings.<br>
   Individual setting can be defined so that it can:<br>
       <ol>
         <li>be validated</li>
         <li>have default value.</li>
      </ol>
   The library provides the following pre-defined setting descriptors:<br>
       <ol>
         <li><code>BooleanSetting</code>: true or false</li>
         <li><code>PatternSetting</code>: pattern in regular expression</li>
         <li><code>RangeSetting</code>: range between 2 integers</li>
         <li><code>ValidValuesSetting</code>: list of valid value strings</li>
      </ol>
   You can define additional setting descriptors by inheriting SettingBase.<br>
   To retrieve a setting value, call a get... method on the setting descriptor, i.e.<br>
      <code>  BooleanSetting bs = new BooleanSetting("consider.holiday", "if consider holiday", false);</code><br>
      <code>  // NOTE: the above must be defined as constant in a class annotated with @RuntimeSettings.</code><br>
      <code>  boolean considerHoliday = bs.getBoolean();</code>
       </li>
</ol>
