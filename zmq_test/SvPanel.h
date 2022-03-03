#include <wx/panel.h>
#include "ZMQServer.h"

/// <summary>
/// Server control panel
/// </summary>
class SvPanel :public wxPanel
{
public:
	ZMQServer* zmqServer;
	BaseDrawPanel* panel;
	wxTextCtrl* svtc;
	SvPanel(wxFrame* parent);
	SvPanel(wxFrame* parent, ZMQServer* zmqServer);
	DECLARE_EVENT_TABLE()
private:
	wxButton* btnRun;
	wxButton* btnPause;
	wxButton* btnCreate;
	wxButton* btnDelete;
	void onCreate(wxCommandEvent& evt);
	void onDelete(wxCommandEvent& evt);
	void onRun(wxCommandEvent& evt);
	void onPause(wxCommandEvent& evt);
	void btnReset(wxCommandEvent& evt);
};

