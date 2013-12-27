#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor,
# Boston, MA 02110-1301, USA.
#

Name:           FullSync
Version:        0.10.2
Release:        0
Summary:        Easy file synchronization for everyone
License:        GPL-2.0+
Group:          Productivity/Archiving/Backup
URL:            http://fullsync.sourceforge.net/
Source0:        %{name}-%{version}-src.tar.gz
%ifarch x86_64
%if 0%{?fedora} > 0
Requires:       java >= 1.6.0
%else
Requires:       java-64 >= 1.6.0
%endif
%else
Requires:       java >= 1.6.0
%endif
%if 0%{?fedora} > 0
Requires:       gtk2%{_isa} >= 2.4.1
%else
Requires:       libgtk-2_0-0%{_isa} >= 2.4.1
%endif
Requires:       xdg-utils
Requires:       xdg-user-dirs
Requires:       desktop-file-utils
BuildRequires:  ant
BuildRequires:  java-devel >= 1.6.0
BuildRequires:  hicolor-icon-theme
BuildRequires:  desktop-file-utils
BuildRequires:  dos2unix
ExclusiveArch:  x86_64 i386 i486 i586 i686
BuildRoot:      %{_tmppath}/%{name}-%{version}-build

%define fsdir %{_javadir}/%{name}-%{version}
# tell RPM not to generate provides for the contained jar files, they are not installed in a public dir
%define _use_internal_dependency_generator 0
%define exename fullsync

%description
FullSync is a universal file synchronization and backup tool
which is highly customizable and expandable. It is especially
for developers, but the basic functionality is easy enough for everyone.

%prep
%setup -q -n %{name}-%{version}

%build
export CLASSPATH=
export OPT_JAR_LIST=:
ant build
desktop-file-edit "--set-key=Exec" "--set-value=%{_bindir}/%{exename}" "build/%{exename}.desktop"
desktop-file-edit "--set-key=Comment" "--set-value=Easy file synchronization for everyone. Version: %{version}" "build/%{exename}.desktop"

%install
cd build/
dos2unix fullsync
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/lib/
install -d -m 755 $RPM_BUILD_ROOT%{fsdir}/images/
install -d -m 755 $RPM_BUILD_ROOT%{_iconsscaldir}
install -d -m 755 $RPM_BUILD_ROOT%{_desktopdir}/
install -d -m 755 $RPM_BUILD_ROOT%{_bindir}/
install -m 644 launcher.jar $RPM_BUILD_ROOT%{fsdir}/launcher.jar
install -m 644 LICENSE $RPM_BUILD_ROOT%{fsdir}/LICENSE
install -m 644 CHANGELOG $RPM_BUILD_ROOT%{fsdir}/CHANGELOG
install -m 755 %{exename} $RPM_BUILD_ROOT%{fsdir}/%{exename}
ln -s %{fsdir}/%{exename} $RPM_BUILD_ROOT%{_bindir}/%{exename}
install -m 644 lib/*.jar $RPM_BUILD_ROOT%{fsdir}/lib/
rm -rf $RPM_BUILD_ROOT%{fsdir}/lib/swt-*
%ifarch x86_64
install -m 644 lib/swt-gtk-linux-x86_64.jar $RPM_BUILD_ROOT%{fsdir}/lib/swt-gtk-linux-x86_64.jar
%else
install -m 644 lib/swt-gtk-linux-x86.jar $RPM_BUILD_ROOT%{fsdir}/lib/swt-gtk-linux-x86.jar
%endif
install -m 644 %{exename}.svg $RPM_BUILD_ROOT%{_iconsscaldir}/%{exename}.svg
install -m 644 %{exename}.desktop $RPM_BUILD_ROOT%{_desktopdir}/%{exename}.desktop
install -m 644 images/* $RPM_BUILD_ROOT%{fsdir}/images/


%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(0644,root,root,0755)
%dir %{fsdir}/
%dir %{fsdir}/lib
%dir %{fsdir}/images
%doc %{fsdir}/LICENSE
%doc %{fsdir}/CHANGELOG
%{fsdir}/launcher.jar
%attr(0755 root root) %{fsdir}/%{exename}
%{_bindir}/%{exename}
%{fsdir}/lib/*.jar
%{fsdir}/images/*
%{_desktopdir}/%{exename}.desktop
%{_iconsscaldir}/%{exename}.svg

%post
%if 0%{?fedora} > 0
/usr/bin/update-desktop-database &> /dev/null || :
%else
%desktop_database_post
%endif
exit 0

%postun
%if 0%{?fedora} > 0
/usr/bin/update-desktop-database &> /dev/null || :
%else
%desktop_database_postun
%endif
exit 0

%changelog
