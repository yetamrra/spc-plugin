package com.example.helloworld;

import com.example.helloworld.DialogManager.DialogNode;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.result.Result;

public interface SLResultListener extends ResultListener
{
	String newResult( Result result, DialogNode context, String tag );
}
