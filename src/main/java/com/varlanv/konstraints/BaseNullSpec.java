package com.varlanv.konstraints;

abstract class BaseNullSpec<ROOT, CURRENT> {

  abstract BaseFieldSpec<ROOT, CURRENT> parent();

  abstract Boolean allowNull();
}
