package main

import (
	"builder/internal/compose"

	cli "github.com/Foxcapades/Argonaut"
	"github.com/Foxcapades/Argonaut/pkg/argo"

	"os"
)

func main() {
	cli.Tree().
		WithCommandGroup(cli.CommandGroup("Shortcuts").
			WithLeaf(cli.Leaf("cb").
				WithDescription("Alias of `compose build`").
				WithCallback(func(_ argo.CommandLeaf) { compose.DefaultBuild() })).
			WithLeaf(cli.Leaf("cu").
				WithDescription("Alias of `compose up`").
				WithCallback(func(_ argo.CommandLeaf) { compose.DefaultUp() })).
			WithLeaf(cli.Leaf("cd").
				WithDescription("Alias of `compose down`").
				WithCallback(func(_ argo.CommandLeaf) { compose.DefaultDown() }))).
		WithCommandGroup(cli.CommandGroup("Subcommands").
			WithBranch(cli.Branch("compose").
				WithAliases("c").
				WithLeaf(cli.Leaf(compose.CmdBuild).
					WithDescription(compose.DescBuild).
					WithCallback(func(_ argo.CommandLeaf) { compose.DefaultBuild() })).
				WithLeaf(cli.Leaf(compose.CmdUp).
					WithDescription(compose.DescUp).
					WithCallback(func(_ argo.CommandLeaf) { compose.DefaultUp() })).
				WithLeaf(cli.Leaf(compose.CmdDown).
					WithDescription(compose.DescDown).
					WithCallback(func(_ argo.CommandLeaf) { compose.DefaultDown() })).
				WithLeaf(cli.Leaf(compose.CmdStart).
					WithDescription(compose.DescStart).
					WithCallback(func(_ argo.CommandLeaf) { compose.DefaultStart() })).
				WithLeaf(cli.Leaf(compose.CmdStop).
					WithDescription(compose.DescStop).
					WithCallback(func(_ argo.CommandLeaf) { compose.DefaultStop() })))).
		MustParse(os.Args)
}
