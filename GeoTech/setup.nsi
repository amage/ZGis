name "geoteck"
OutFile "geoteckdemo.exe"
InstallDir $PROGRAMFILES\GeoTeckDemo
; 4 vista
RequestExecutionLevel user

Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\GeoTeckDemo "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoTeckDemo" "DisplayName" "GeoTeck Demo"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoTeckDemo" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoTeckDemo" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoTeckDemo" "NoRepair" 1
  WriteUninstaller "uninstall.exe"  
  ; Put file there
  File /r geoteck\*.*
 
SectionEnd ; end the section

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\GeoTeckDemo"
  CreateShortCut "$SMPROGRAMS\GeoTeckDemo\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\GeoTeckDemo\GeoTeckDemo.lnk" "$INSTDIR\start.bat" "" "$INSTDIR\start.bat" 0
SectionEnd

; Optional section (can be disabled by the user)
;Section "maps"
;  CreateDirectory $INSTDIR\maps
;  SetOutPath $INSTDIR\maps
;  File /r C:\maps\*.*
;SectionEnd

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoTeckDemo"
  DeleteRegKey HKLM SOFTWARE\GeoTeckDemo

  ; Remove files and uninstaller
  ;Delete $INSTDIR\*.*
  Delete $INSTDIR\uninstall.exe

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\GeoTeckDemo\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\GeoTeckDemo"
  RMDir /r "$INSTDIR"

SectionEnd