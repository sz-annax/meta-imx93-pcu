SUMMARY = "ACC rootfs upgrade package generator"
DESCRIPTION = "Generates ACC upgrade tarball from existing deploy images"
LICENSE = "CLOSED"

SRC_URI = "file://gen_pcu.py"

S = "${UNPACKDIR}"

inherit python3native

TARGET_NAME ?= "ACC"
TARGET_TYPE ?= "ACC"
IMAGE_TYPE ?= "rootfs"

IMAGES_DIR ?= "${DEPLOY_DIR_IMAGE}"
OUT_DIR ?= "${TMPDIR}/annax"

do_compile() {
    echo "==== rootfs package generation ===="
    echo "IMAGES_DIR = ${IMAGES_DIR}"
    echo "OUT_DIR    = ${OUT_DIR}"

    if [ ! -d "${IMAGES_DIR}" ]; then
        bbfatal "Image directory not found: ${IMAGES_DIR}"
    fi

    ${PYTHON} ${UNPACKDIR}/gen_pcu.py \
        --output-dir "${OUT_DIR}" \
        --image-type "${IMAGE_TYPE}" \
        --target-version "${PV}" \
        --target-name "${TARGET_NAME}" \
        --target-type "${TARGET_TYPE}" \
        --rootfs "${IMAGES_DIR}/acc-image-imx93-pcu.rootfs.tar.zst"

}

do_install[noexec] = "1"
do_compile[nostamp] = "1"
