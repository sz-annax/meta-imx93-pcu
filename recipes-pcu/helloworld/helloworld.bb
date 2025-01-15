DESCRIPTION = "Prints Hello World"

SRC_URI = "file://helloworld.c"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

do_compile() {
    ${CC} ${LDFLAGS} ../helloworld.c -o helloworld
}

do_install() {
    cat << EOF > ${WORKDIR}/helloworld-${PV}/helloworld.service  
[Unit]
Description=Hello World Service
Wants=network.target

[Service]
Type=simple
TimeoutSec=300
ExecStart=/usr/bin/helloworld
Restart=no

[Install]
WantedBy=multi-user.target
EOF

    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system

    install -m 0755 helloworld ${D}${bindir}
    install -m 0644 ${S}/helloworld.service ${D}/${systemd_unitdir}/system
}

FILES_${PN} = "${bindir}/helloworld"

inherit systemd
SYSTEMD_SERVICE:${PN} = "helloworld.service"
SYSTEMD_AUTO_ENABLE = "enable"
