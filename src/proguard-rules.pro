# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.pizzashi.cremer.Cremer {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/pizzashi/cremer/repack'
-flattenpackagehierarchy
-dontpreverify
