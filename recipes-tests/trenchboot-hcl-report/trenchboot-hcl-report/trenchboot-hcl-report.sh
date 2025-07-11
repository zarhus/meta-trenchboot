#!/bin/bash --

# Based on work by The Qubes OS Project, https://www.qubes-os.org
#
# Copyright (C) 2013 Laszlo Zrubecz <mail@zrubi.hu>
# Copyright (C) 2025 3mdeb
#
# This script is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This script is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this script; if not, see <https://www.gnu.org/licenses/>.

set -uo pipefail
VERSION=0.1

echo -e "trenchboot-hcl-report v$VERSION"
echo ""
echo "This tool is used to gather basic hardware information for the TrenchBoot HCL (Hardware Compatibility List)"
echo ""
echo -e "Usage:\ttrenchboot-hcl-report"
echo -e "\t\t\tWARNING: The HCL logs may contain numerous hardware details, including serial numbers."
echo -e "\t\t\tIf, for privacy or security reasons, you do not wish to make this information public, "
echo -e "\t\t\tplease do not send the .cpio.gz file to the public repository."
echo ""

# Function to run a command and log output and error
run_and_log() {
    local cmd="$1"
    local outfile="$2"
    local base_name
    base_name=$(basename "$outfile")

    echo "Running: $cmd"
    bash -c "$cmd" > "$TEMP_DIR/${base_name}.log" 2> "$TEMP_DIR/${base_name}.err.log"
}

check_drtm_event() {
  local TPM_PATH="/sys/class/tpm/tpm0"
  local ALGO
  local PCR_17
  local PCR_18
  local VALID=true

  # Set algorithm path based on TPM version
  if [ "$TPM_S" = "2.0" ]; then
    ALGO="sha256"
  elif [ "$TPM_S" = "1.2" ]; then
    ALGO="sha1"
  else
    return 2
  fi

  local PCR_PATH="$TPM_PATH/pcr-$ALGO"
  PCR_17=$(cat "$PCR_PATH/17" 2>/dev/null | tr '[:upper:]' '[:lower:]')
  PCR_18=$(cat "$PCR_PATH/18" 2>/dev/null | tr '[:upper:]' '[:lower:]')

  if [[ -z "$PCR_17" || -z "$PCR_18" ]]; then
    return 3
  fi

  # Check if PCR values are all zeros or all FF
  if [[ "$PCR_17" =~ ^(0{40,}|f{40,})$ ]]; then
    VALID=false
  fi
  if [[ "$PCR_18" =~ ^(0{40,}|f{40,})$ ]]; then
    VALID=false
  fi

  if [ "$VALID" = true ]; then
    # DRTM event likely present
    return 0
  else
    # DRTM event not detected
    return 1
  fi
}

BRAND="$(dmidecode -s system-manufacturer)"
PRODUCT="$(dmidecode -s system-product-name)"
BIOS="$(dmidecode -s bios-version)"
TYPE="$(dmidecode -s chassis-type)"

DATE=$(date +%Y%m%d-%H%M%S) || exit

# Create temporary directory
TEMP_DIR=$(mktemp --tmpdir -d HCL.XXXXXXXXXX) || exit
case $TEMP_DIR in (/*) :;; (*) TEMP_DIR=./$TEMP_DIR;; esac

# Collect data
run_and_log "cat /proc/cpuinfo" "cpuinfo"
run_and_log "lspci -nnvk" "lspci"
run_and_log "dmidecode" "dmidecode"
run_and_log "dmesg" "dmesg"
run_and_log "find $(realpath /sys/class/tpm/tpm*) -type f -print -exec cat {} \;" "tpm_version"

# Xen-only
if [ -f /sys/hypervisor/type ] && grep -q '^xen$' /sys/hypervisor/type; then
  XEN="true"
else
  XEN="false"
fi

BOOT_FLOW="linux"
if [[ "$XEN" == "true" ]]; then
  BOOT_FLOW="xen"
  run_and_log "xl info" "xl-info"
  run_and_log "xl dmesg" "xl-dmesg"

  XEN_MAJOR=$(grep xen_major "$TEMP_DIR/xl-info.log"|cut -d: -f2 |tr -d ' ')
  XEN_MINOR=$(grep xen_minor "$TEMP_DIR/xl-info.log"|cut -d: -f2 |tr -d ' ')
  XEN_EXTRA=$(grep xen_extra "$TEMP_DIR/xl-info.log"|cut -d: -f2 |tr -d ' ')
fi

# Intel-only
CPU=$(grep "model name" "$TEMP_DIR/cpuinfo.log" |sort -u |cut -d ' ' -f3- |sed -e "s/[[:space:]]*/\  /")
if [[ "${CPU,,}" == *"intel"* ]]; then
  INTEL="true"
else
  INTEL="false"
fi

if [[ "$INTEL" == "true" ]]; then
  run_and_log "txt-suite exec-tests" "txt-suite"
  run_and_log "txt-stat" "txt-stat"
  run_and_log "txt-parse_err" "txt-parse_err"
fi

TB_DISTRO_VER=$(grep '^VERSION_ID=' /etc/os-release | cut -d= -f2 | tr -d '"')
KERNEL=$(uname -r |cut -d '.' -f-3)
CHIPSET=$(grep "00:00.0.*Host bridge" "$TEMP_DIR/lspci.log" |cut -d ':' -f3- |sed -e "s/[[:space:]]*/\  /")

FILENAME="TrenchBoot-HCL-${BRAND//[^[:alnum:]]/_}-${PRODUCT//[^[:alnum:]]/_}-$DATE"

if [[ -f "/sys/class/tpm/tpm0/tpm_version_major" && $(< "/sys/class/tpm/tpm0/tpm_version_major") == "2" ]]
  then
    TPM="Device present (TPM 2.0)"
    TPM_S="2.0"
  else
    if [[ -f "/sys/class/tpm/tpm0/pcrs" ]]
      then
        TPM="Device present (TPM 1.2)"
        TPM_S="1.2"
      else
        TPM="Device not found"
        TPM_S="unknown"
    fi
fi

check_drtm_event
DRTM_TEST_RESULT=$?

case $DRTM_TEST_RESULT in
  # DRTM event detected
  0)
    TB_SUCCESS="yes"
    ;;
  # DRTM event not detected
  1)
    TB_SUCCESS="no"
    ;;
  # failed to get PCR data from TPM
  *)
    TB_SUCCESS="unknown"
    ;;
esac

READABLE_OUTPUT="
TrenchBoot success: $TB_SUCCESS

Brand:\t\t$BRAND
Model:\t\t$PRODUCT
BIOS:\t\t$BIOS

CPU:
$CPU
Chipset:
$CHIPSET

TPM:\t\t$TPM
"

YAML_OUTPUT="---
layout:
  'hcl'
type:
  '$TYPE'
tpm:
  '$TPM_S'
brand: |
  $BRAND
model: |
  $PRODUCT
bios: |
  $BIOS
cpu: |
$CPU
cpu-short: |
  FIXME
chipset: |
$CHIPSET
chipset-short: |
  FIXME
versions:
  - works:
      $TB_SUCCESS
    tb-distro: |
      $TB_DISTRO_VER
    boot-flow:
      $BOOT_FLOW
    kernel: |
      $KERNEL
    remark: |
      FIXME
    credit: |
      FIXAUTHOR"

# Conditionally append Xen info
if [ "$XEN" = true ]; then
  YAML_OUTPUT+="
    xen: |
      $XEN_MAJOR.$XEN_MINOR$XEN_EXTRA
"
fi

echo
echo -e "HCL summary:"
echo -e "$READABLE_OUTPUT"

# cpio
cd -- "$TEMP_DIR" || exit
find -print0 | cpio --quiet -o -H crc --null | gzip  > "$HOME/$FILENAME.cpio.gz"
cd || exit

echo -e "$YAML_OUTPUT" >> "$HOME/$FILENAME.yml"
echo -e "HCL info saved to: $FILENAME.yml"
echo -e "Logs saved to: $FILENAME.cpio.gz"
echo

# cpio
cd -- "$TEMP_DIR" || exit
find -print0 | cpio --quiet -o -H crc --null | gzip  > "$HOME/$FILENAME.cpio.gz"
cd || exit

# cleanup
if [[ -d $TEMP_DIR ]]
 then
   rm -rf -- "$TEMP_DIR"
fi
