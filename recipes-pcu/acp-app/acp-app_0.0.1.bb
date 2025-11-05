SUMMARY = "ACP Python Application Service"
DESCRIPTION = "ACP Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/acp-app.git;protocol=ssh;branch=main \
           file://acp-app.service \
           file://my-echo-cancel.conf \ 
        "
SRCREV = "89c2c10e7f57ed30678fe86b00b07805f22cc51e"

S = "${WORKDIR}/git"

do_install() {
    # delete unnecessary files and folders
    rm -rf ${S}/docs ${S}/script

    # /usr/share/acp-app
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/* ${D}${datadir}/${PN}/

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service ${D}${systemd_system_unitdir}/

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

FILES_${PN} = "${bindir}/${PN}.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "acp-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
