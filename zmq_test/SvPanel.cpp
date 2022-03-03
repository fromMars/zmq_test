#include "SvPanel.h"

// const ids
const long idCreate = wxNewId();
const long idDelete = wxNewId();
const long idRun = wxNewId();
const long idPause = wxNewId();

/// <summary>
/// Constructor without server for debug purpose
/// </summary>
/// <param name="parent"></param>
SvPanel::SvPanel(wxFrame* parent)
	:wxPanel(parent)
{
	// zmqServer = new ZMQServer(NULL, NULL);
	wxBoxSizer* sizer = new wxBoxSizer(wxVERTICAL);

	btnCreate = new wxButton(this, idCreate, "Create");
	btnDelete = new wxButton(this, idDelete, "Delete");
	btnRun = new wxButton(this, idRun, "Run");
	btnPause = new wxButton(this, idPause, "Pause");

	sizer->Add(btnCreate, 1, 1);
	sizer->Add(btnDelete, 1, 1);
	sizer->Add(btnRun, 1, 1);
	sizer->Add(btnPause, 1, 1);
	this->SetSizer(sizer);
}

/// <summary>
/// This Constructor shall work fine
/// </summary>
/// <param name="zmqServer"></param>
SvPanel::SvPanel(wxFrame* parent, ZMQServer* zmqServer)
	:wxPanel(parent)
{
	this->zmqServer = zmqServer;
	panel = zmqServer->bdPanel;
	svtc = zmqServer->svtc;
	wxBoxSizer* sizer = new wxBoxSizer(wxVERTICAL);

	btnCreate = new wxButton(this, idCreate, "Create");
	btnDelete = new wxButton(this, idDelete, "Delete");
	btnRun = new wxButton(this, idRun, "Run");
	btnPause = new wxButton(this, idPause, "Pause");
	btnCreate->Disable();

	sizer->Add(btnCreate, 1, 1);
	sizer->Add(btnDelete, 1, 1);
	sizer->Add(btnRun, 1, 1);
	sizer->Add(btnPause, 1, 1);
	this->SetSizer(sizer);
}

/// <summary>
/// Event handlers
/// Use these handlers to 
/// Create, Delete, Run, Pause a server instance
/// </summary>
/// <param name="evt"></param>
/// <summary>
/// Create
/// By Initializing this should not used, server thread will created
/// in onRun method;
/// And when original thread were deleted, this method may used to 
/// create next new thread. 
/// </summary>
/// <param name="evt"></param>
void SvPanel::onCreate(wxCommandEvent& evt)
{
	zmqServer = new ZMQServer(svtc, panel);
	btnCreate->Disable();
	btnRun->Enable(true);
	((wxFrame*)zmqServer->svtc->GetParent())->
		GetStatusBar()->SetStatusText("server created", 1);
	zmqServer->svtc->AppendText("server created\n");
}

/// <summary>
/// Delete
/// Delete the thread, so zmqServer instance would not
/// exist anymore, this may cause GUI freezing, so do
/// not use this method for now.
/// </summary>
/// <param name="evt"></param>
void SvPanel::onDelete(wxCommandEvent& evt)
{
	try
	{
		zmqServer->Delete();
		btnCreate->Enable(true);
		((wxFrame*)zmqServer->svtc->GetParent())->
			GetStatusBar()->SetStatusText("server deleted", 1);
		zmqServer->svtc->AppendText("server deleted\n");
	}
	catch (std::exception e)
	{
		wxMessageBox(e.what());
		zmqServer->svtc->AppendText(e.what());
	}
}

/// <summary>
/// Run
/// Create a new thread and run, if the thread
/// is paused, resume it first
/// </summary>
/// <param name="evt"></param>
void SvPanel::onRun(wxCommandEvent& evt)
{
	/*wxMessageBox("running a thread");*/
	if (zmqServer->IsPaused())
		zmqServer->Resume();
	else
		zmqServer->runServer();
	btnRun->Disable();
	btnPause->Enable(true);
	((wxFrame*)zmqServer->svtc->GetParent())->
		GetStatusBar()->SetStatusText("server running", 1);
	zmqServer->svtc->AppendText("server running\n");
}

/// <summary>
/// Pause
/// pause a running thread
/// </summary>
/// <param name="evt"></param>
void SvPanel::onPause(wxCommandEvent& evt)
{
	if (zmqServer->IsRunning() && zmqServer->IsAlive())
	{
		zmqServer->Pause();
		btnPause->Disable();
		btnRun->Enable(true);

		((wxFrame*)zmqServer->svtc->GetParent())->
			GetStatusBar()->SetStatusText("server paused", 1);
		zmqServer->svtc->AppendText("server paused\n");
	}
}

void SvPanel::btnReset(wxCommandEvent& evt)
{
	btnCreate->Enable(true);
	/*btnDelete->Enable(true);
	btnRun->Enable(true);
	btnPause->Enable(true);*/
}

///
/// Event table
/// 
BEGIN_EVENT_TABLE(SvPanel, wxPanel)
// event btn click
//EVT_BUTTON(idCreate, SvPanel::onCreate)
//EVT_BUTTON(idDelete, SvPanel::onDelete)
EVT_BUTTON(idRun, SvPanel::onRun)
EVT_BUTTON(idPause, SvPanel::onPause)
EVT_COMMAND(ID_RESET_BUTTON, wxEVT_COMMAND_TEXT_UPDATED, SvPanel::btnReset)
END_EVENT_TABLE()