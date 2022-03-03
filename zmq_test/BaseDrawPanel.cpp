#include "BaseDrawPanel.h"
#include "ZMQServer.h"

BaseDrawPanel::BaseDrawPanel(wxFrame* parent)
	:wxPanel(parent) {}

void rcvDrawText(BaseDrawPanel* bdp)
{
	// get info
	wxPaintDC dc(bdp);
	wxDateTime dt(wxDateTime::Now());
	wxString tmp;
	if (dt.IsValid())
	{
		tmp << dt.FormatTime();
	}
	else
	{
		tmp << wxT("not valid");
	}

	// get panel size
	// x, y transformed
	float posX, posY;
	//wxSize sz = bdp->GetSize();
	posX = 20;
	posY = 5;

	dc.DrawText(tmp, posY, posX);
}

void BaseDrawPanel::render(wxPaintEvent& evt)
{
	rcvDrawText(this);
}

void BaseDrawPanel::onRender(wxCommandEvent& evt)
{
	rcvDrawText(this);
	this->Refresh();
}

// event handlers
void BaseDrawPanel::mouseMoved(wxMouseEvent& event)
{
}

void BaseDrawPanel::mouseDown(wxMouseEvent& event)
{
	rcvDrawText(this);
	this->Refresh();
}

void BaseDrawPanel::mouseWheelMoved(wxMouseEvent& event)
{
}

void BaseDrawPanel::mouseRelease(wxMouseEvent& event)
{
}

void BaseDrawPanel::rightClick(wxMouseEvent& event)
{
}

void BaseDrawPanel::mouseLeftWindow(wxMouseEvent& event)
{
}

void BaseDrawPanel::keyPressed(wxMouseEvent& event)
{
}

void BaseDrawPanel::keyReleased(wxMouseEvent& event)
{
}

// event table
BEGIN_EVENT_TABLE(BaseDrawPanel, wxPanel)
EVT_MOTION(BaseDrawPanel::mouseMoved)
EVT_LEFT_DOWN(BaseDrawPanel::mouseDown)
EVT_LEFT_UP(BaseDrawPanel::mouseRelease)
EVT_RIGHT_DOWN(BaseDrawPanel::rightClick)
EVT_LEAVE_WINDOW(BaseDrawPanel::mouseLeftWindow)
//EVT_KEY_DOWN(BaseDrawPanel::keyPressed)
//EVT_KEY_UP(BaseDrawPanel::keyPressed)
EVT_MOUSEWHEEL(BaseDrawPanel::mouseWheelMoved)
EVT_PAINT(BaseDrawPanel::render)
// event in thread when starting server
EVT_COMMAND(RENDER_ID, wxEVT_COMMAND_TEXT_UPDATED, BaseDrawPanel::onRender)
END_EVENT_TABLE()

